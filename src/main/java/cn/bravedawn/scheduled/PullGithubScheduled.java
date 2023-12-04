package cn.bravedawn.scheduled;

import cn.bravedawn.scheduled.dto.ArticleDTO;
import cn.bravedawn.scheduled.dto.GithubContent;
import cn.bravedawn.scheduled.markdown.KeyNodeVisitor;
import cn.bravedawn.web.config.GithubConfig;
import cn.bravedawn.web.db.JasperTransactionManager;
import cn.bravedawn.web.mbg.mapper.ArticleMapper;
import cn.bravedawn.web.mbg.mapper.ArticleTagRelationMapper;
import cn.bravedawn.web.mbg.mapper.TagMapper;
import cn.bravedawn.web.mbg.model.Article;
import cn.bravedawn.web.mbg.model.ArticleTagRelation;
import cn.bravedawn.web.mbg.model.Tag;
import cn.bravedawn.web.util.Base64Util;
import cn.bravedawn.web.util.CollectionUtil;
import cn.bravedawn.web.util.FileUtils;
import cn.bravedawn.web.util.ShaUtil;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/2/1 15:50
 */

@Component
public class PullGithubScheduled {

    private static final Logger log = LoggerFactory.getLogger(PullGithubScheduled.class);

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private ArticleTagRelationMapper articleTagRelationMapper;

    @Autowired
    private GithubConfig githubConfig;

    private static final ObjectMapper mapper;

    // 保存批量执行过程中的文章信息，避免批量重复执行
    private static ThreadLocal<List<GithubContent>> githubContentListThreadLocal = new ThreadLocal<>();

    static {
        // 配置自定义的反序列化器
        mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(List.class, new CustomDeserializer());
        module.addDeserializer(GithubContent.class, new CustomFileDeserializer());
        mapper.registerModule(module);
    }


    /**
     * 这段批量的逻辑简单来叙述一下：
     * 1. 拉取github上面的数据，并逐步递归迭代，获取到所有的文章信息
     * 2. 保存文章
     *  1）判断是否为新文章，若为新文章则直接插入数据库，并维护标签和文章的关系
     *  2) 判断是否需要更新文章，这里根据github返回的sign字段来进行判断，若更新了文章，则会更新文章到数据库，并维护标签和文章的关系
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void runTask() {
        try {
            // 配置批量日志打印关键字
            MDC.put("appId", "jasper");
            MDC.put("tradeName", "JOB-PullGithubScheduled");
            MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));

            log.info("---开始批量拉取Github上的数据---");
            // 拉取数据
            List<GithubContent> list = mapper.readValue(pullData("", false), List.class);
            for (GithubContent item : list) {
                checkFile(item);
            }

            // 将文章信息存库
            saveGithubContentList();

            log.info("---拉取Github上的数据结束---");
        } catch (Throwable e) {
            log.error("定时任务失败，请稍后再试.", e);
        } finally {
            githubContentListThreadLocal.remove();
            MDC.clear();
        }
    }


    /**
     * 保存文章
     */
    private void saveGithubContentList() {
        List<GithubContent> githubContentList = githubContentListThreadLocal.get();
        for (GithubContent content : githubContentList) {
            if (content.getIsExist()) {
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
     * 解析文章信息
     * @param content 内容
     * @return
     */
    private ArticleDTO buildArticle(GithubContent content) {
        Article article = new Article();
        String articleContent = Base64Util.decode(content.getContent());
        String sign = ShaUtil.sign(content.getPath());

        // 解析markdown
        Parser parser = Parser.builder().build();
        Node document = parser.parse(articleContent);

        // 解析标签和介绍、下载图片
        KeyNodeVisitor keyNodeVisitor = new KeyNodeVisitor(ShaUtil.sign(content.getPath()));
        document.accept(keyNodeVisitor);

        // 将更新后的文档进行重新渲染
        HtmlRenderer renderer = HtmlRenderer.builder().build();
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
            article.setContent(Matcher.quoteReplacement(renderHtml));
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
     * 如果是已经存在数据库的文章，我们称为老数据
     * @param content 文章内容
     */
    private void oldestArticleHandler(GithubContent content) {
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
            transactionManager.rollback();
            log.error("批量拉取数据事务回滚", e);
        }
    }


    /**
     * 如果是新文章
     * @param content 文章内容
     */
    private void newestArticleHandler(GithubContent content) {
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
            transactionManager.rollback();
            log.error("批量拉取数据事务回滚", e);
        }
    }


    /**
     * 从github拉取数据
     * @param path 请求地址
     * @param isDirect 是否为直接请求的地址，如果是目录则需要加签名的前缀，如果是文章则为直接请求的目录
     * @return
     * @throws IOException
     */
    private String pullData(String path, boolean isDirect) throws IOException {
        // 遍历项目目录，解析到最深层文件目录
        // 1.发送请求获取根目录信息
        // 设置超时时间
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig connConfig = RequestConfig.custom()
                .setConnectTimeout(20000)
                .setSocketTimeout(20000)
                .build();

        String url = isDirect ? path : githubConfig.getRepoUrl() + path;
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Authorization", githubConfig.getAccessToken());
        httpget.setConfig(connConfig);

        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                log.error("请求Github报错, url={}, status={}, body={}.", httpget.getURI(), status,
                        EntityUtils.toString(response.getEntity()));
                return null;
            }
        };

        String responseBody = httpClient.execute(httpget, responseHandler);
        return responseBody;

    }

    /**
     * 校验文章是新文章还是老文件
     * @param content
     * @throws Exception
     */
    public void checkFile(GithubContent content) throws Exception {

        List<GithubContent> githubContents = new ArrayList<>();

        if (content.getType().equals("file") && FileUtils.getFileSuffix(content.getName()).equals(".md")) {
            log.info("拉取文章内容, path={}.", content.getPath());
            String fileJson = pullData(content.getUrl(), true);
            if (StringUtils.isNotBlank(fileJson)) {
                GithubContent item = mapper.readValue(fileJson, GithubContent.class);
                // 判断是否是新文章，若是新文章则添加到githubContentList里，若不是则跳过
                if (!isExistDatabase(item.getPath())) {
                    log.info("[{}]是新文章, path={}.", item.getName(), item.getPath());
                    item.setIsExist(false);
                } else {
                    log.info("[{}]是老文章, path={}.", item.getName(), item.getPath());
                    item.setIsExist(true);
                }
                githubContents.add(item);
            }
        } else if (content.getType().equals("dir")) {
            // 如果不是file就继续往下一层走
            String responseStr = pullData(content.getPath(), false);
            List<GithubContent> list = mapper.readValue(responseStr, List.class);
            for (GithubContent item : list) {
                checkFile(item);
            }
        }

        // 判断当前线程是否存在需要变更的文章
        if (!githubContents.isEmpty()) {
            if (githubContentListThreadLocal.get() == null) {
                githubContentListThreadLocal.set(githubContents);
            } else {
                githubContentListThreadLocal.get().addAll(githubContents);
            }
        }

    }

    /**
     * 判断该文件是否在数据库中，这里我们其实是通过github返回的文章path字段，加签之后和数据库中article表的sign字段进行匹配
     * @param path 在的话-返回true, 不在返回false
     * @return
     */
    private boolean isExistDatabase(String path) {
        String sign = ShaUtil.sign(path);
        if (StringUtils.isNotBlank(sign)) {
            int count = articleMapper.selectCountByMd5(sign);
            return count != 0;
        } else {
            return true;
        }
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
