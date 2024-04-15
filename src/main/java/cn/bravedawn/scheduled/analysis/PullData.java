package cn.bravedawn.scheduled.analysis;

import cn.bravedawn.scheduled.PullGithubScheduled;
import cn.bravedawn.scheduled.dto.ArticleDTO;
import cn.bravedawn.scheduled.dto.Content;
import cn.bravedawn.scheduled.markdown.ImageNodeRenderer;
import cn.bravedawn.scheduled.markdown.KeyNodeVisitor;
import cn.bravedawn.web.db.JasperTransactionManager;
import cn.bravedawn.web.mbg.mapper.ArticleMapper;
import cn.bravedawn.web.mbg.mapper.ArticleTagRelationMapper;
import cn.bravedawn.web.mbg.mapper.TagMapper;
import cn.bravedawn.web.mbg.model.Article;
import cn.bravedawn.web.mbg.model.ArticleTagRelation;
import cn.bravedawn.web.mbg.model.Tag;
import cn.bravedawn.web.util.Base64Util;
import cn.bravedawn.web.util.CollectionUtil;
import cn.bravedawn.web.util.ShaUtil;
import org.apache.commons.lang.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/21 11:42
 */

@Component
public abstract class PullData {

    private static final Logger log = LoggerFactory.getLogger(PullGithubScheduled.class);

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelationMapper articleTagRelationMapper;

    @Value("${jasper.pullData.repo}")
    private String pullDataRepo;

    private HtmlRenderer renderer;
    private Parser parser;

    abstract List<? extends Content> loadData();


    @PostConstruct
    public void init() {
        // 配置表格扩展
        List<Extension> extensions = Arrays.asList(TablesExtension.create());
        parser = Parser.builder()
                .extensions(extensions)
                .build();
        renderer = HtmlRenderer.builder()
                // 自定义图片渲染
                .nodeRendererFactory(new HtmlNodeRendererFactory() {
                    @Override
                    public NodeRenderer create(HtmlNodeRendererContext context) {
                        return new ImageNodeRenderer(context);
                    }
                })
                .extensions(extensions)
                .build();

    }

    public void storeArticle() {
        List<? extends Content> contents = loadData();
        // 将文章信息存库
        saveGithubContentList(contents);
    }

    /**
     * 保存文章
     */
    private void saveGithubContentList(List<? extends Content> contents) {
        for (Content content : contents) {
            if (content.isExist()) {
                // 老文章的处理逻辑
                log.info("更新老文章的内容, title={}.", content.getName());
                oldestArticleHandler(content);
            } else {
                // 新文章的处理逻辑
                log.info("插入新文章的内容, title={}.", content.getName());
                newestArticleHandler(content);
            }
        }
    }

    /**
     * 判断该文件是否在数据库中，这里我们其实是通过github返回的文章path字段，加签之后和数据库中article表的sign字段进行匹配
     * @param path 在的话-返回true, 不在返回false
     * @return
     */
    protected boolean isExistDatabase(String path) {
        String sign = ShaUtil.sign(path);
        if (StringUtils.isNotBlank(sign)) {
            int count = articleMapper.selectCountByMd5(sign);
            return count != 0;
        } else {
            return true;
        }
    }

    /**
     * 如果是已经存在数据库的文章，我们称为老数据
     * @param content 文章内容
     */
    private void oldestArticleHandler(Content content) {
        /**
         * 如果是老文章
         *  1.更新文章内容
         *  2.插入标签
         *  3.判断标签的结构是否发生变化，若发生则删除原有文章的标签相关的关联，重新插入标签结构
         */
        // 判断文章是否需要更新
        boolean updateDatabase = isUpdateDatabase(content.getSha(), content.getPath());
        if (!updateDatabase) {
            log.info("文章内容并没有发生改变，暂不进行更新. title={}, sha={}.", content.getName(), content.getSha());
            return;
        }

        // 解析文章信息
        ArticleDTO articleDTO = buildArticle(content);
        if (articleDTO == null) {
            return;
        }

        // 手动事务
        JasperTransactionManager transactionManager = new JasperTransactionManager();
        try {
            Article article = articleDTO.getArticle();
            articleMapper.updateSelective(article);

            article = articleMapper.selectBySign(article.getSign());
            // 文章最新的标签
            List<Tag> tags = buildTags(articleDTO.getTag());
            List<String> newTags = tags.stream().map(Tag::getName).toList();

            // 判断文章的标签是否发生变化
            List<String> dataTags = articleTagRelationMapper.selectTagNameByArticleId(article.getId());
            if (!CollectionUtil.judgeEquals(newTags, dataTags)) {
                // 若发生变化则删除原有的标签关联，重新插入
                articleTagRelationMapper.deleteByArticle(article.getId());
                buildArticleTagsRelation(article.getId(), tags);
            }

            // 提交事务
            transactionManager.commit();
        } catch (Throwable e) {
            // 回滚事务
            log.error("批量拉取数据事务回滚", e);
            transactionManager.rollback();
        }
    }


    /**
     * 如果是新文章
     * @param content 文章内容
     */
    private void newestArticleHandler(Content content) {
        ArticleDTO articleDTO = buildArticle(content);
        if (articleDTO == null) {
            return;
        }
        Article article = articleDTO.getArticle();

        // 手动事务
        JasperTransactionManager transactionManager = new JasperTransactionManager();
        try {
            log.info("文章信息: article={}.", article);
            articleMapper.insertSelective(article);
            List<Tag> tags = buildTags(articleDTO.getTag());

            buildArticleTagsRelation(article.getId(), tags);
            // 提交事务
            transactionManager.commit();
        } catch (Throwable e) {
            // 回滚事务
            log.error("批量拉取数据事务回滚", e);
            transactionManager.rollback();
        }
    }


    /**
     * 解析文章信息
     * @param content 内容
     * @return
     */
    private ArticleDTO buildArticle(Content content) {
        Article article = new Article();
        String articleContent = Base64Util.decode(content.getContent());
        String sign = ShaUtil.sign(content.getPath());

        // 解析markdown
        Node document = parser.parse(articleContent);

        // 解析标签和介绍、下载图片
        KeyNodeVisitor keyNodeVisitor = new KeyNodeVisitor(ShaUtil.sign(content.getPath()), pullDataRepo);
        document.accept(keyNodeVisitor);

        // 将更新后的文档进行重新渲染
        String renderHtml = renderer.render(document);

        // 从正文中移除标签和介绍
        List<String> info = keyNodeVisitor.getKeyNodeInfo();
        if(info.size() == 2) {
            ArticleDTO articleDTO = new ArticleDTO();
            // 设置标签
            if (StringUtils.isNotBlank(info.get(0))) {
                articleDTO.setTag(info.get(0));
            }
            // 整理article信息
            int index = content.getName().lastIndexOf(".");
            article.setTitle(content.getName().substring(0, index));
            article.setIntro(info.get(1));
            article.setAuthor("depers");
            // 处理sql插入的$符号
            article.setContent(Base64.getEncoder().encodeToString(renderHtml.getBytes(StandardCharsets.UTF_8)));
            article.setSign(sign);
            article.setPath(content.getPath());
            article.setSha(content.getSha());
            articleDTO.setArticle(article);
            return articleDTO;
        } else {
            return null;
        }
    }

    /**
     * 构建标签
     * @param keyword
     * @return
     */
    private List<Tag> buildTags(String keyword) {
        // 整理tag信息
        List<String> tagList = Arrays.asList(keyword.split("/"));
        List<Tag> maintainTags = new ArrayList<>();
        List<Tag> waitTags = new ArrayList<>();
        for (String name : tagList) {
            // 判断该tag是否在数据库中，若在则不插入数据库
            Tag existTag = tagMapper.selectCount(name);
            if (existTag != null) {
                maintainTags.add(existTag);
                continue;
            }

            Tag tag = new Tag();
            tag.setName(name);
            waitTags.add(tag);
        }

        if (!waitTags.isEmpty()) {
            tagMapper.batchInsert(waitTags);
        }
        maintainTags.addAll(waitTags);
        return maintainTags;
    }


    /**
     * 维护文章和标签之间的关系
     * @param articleId 文章id
     * @param maintainTags 标签信息
     */
    private void buildArticleTagsRelation(long articleId, List<Tag> maintainTags) {
        // 维护文章和标签的关系表
        List<ArticleTagRelation> articleTagRelationList = new ArrayList<>();
        for (Tag tag : maintainTags) {
            ArticleTagRelation articleTagRelation = new ArticleTagRelation();
            articleTagRelation.setArticleId(articleId);
            articleTagRelation.setTagId(tag.getId());
            articleTagRelationList.add(articleTagRelation);
        }
        articleTagRelationMapper.batchInsert(articleTagRelationList);

    }

    /**
     * 查询是否文章的内容是否有更新
     * @param sha github接口返回的sha值
     * @param path github接口返回的path
     * @return
     */
    private boolean isUpdateDatabase(String sha, String path) {
        String sign = ShaUtil.sign(path);
        if (StringUtils.isNotBlank(sha) && StringUtils.isNotBlank(sign)) {
            String shaStr = articleMapper.selectShaByMd5(sign);
            return !sha.equals(shaStr);
        } else {
            return true;
        }
    }

}
