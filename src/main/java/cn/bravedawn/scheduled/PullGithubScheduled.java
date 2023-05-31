package cn.bravedawn.scheduled;

import cn.bravedawn.scheduled.dto.GithubContent;
import cn.bravedawn.scheduled.dto.KeyNodeInfo;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    private static List<GithubContent> githubContentList = new ArrayList<>();

    static {
        // 配置自定义的反序列化器
        mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(List.class, new CustomDeserializer());
        module.addDeserializer(GithubContent.class, new CustomFileDeserializer());
        mapper.registerModule(module);
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void runTask() {
        try {
            // 配置批量日志打印关键字
            MDC.put("appId", "jasper");
            MDC.put("tradeName", "JOB-PullGithubScheduled");
            MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));

            log.info("---开始批量拉取Github上的数据---");
            // 拉取数据
            List<GithubContent> list = mapper.readValue(pullData(""), List.class);
            for (GithubContent item : list) {
                checkFile(item);
            }
            // 将文章信息存库
            saveGithubContentList(githubContentList);
            log.info("---拉取Github上的数据结束---");
        } catch (Throwable e) {
            log.error("定时任务失败，请稍后再试.", e);
        } finally {
            githubContentList.clear();
            MDC.clear();
        }
    }


    /**
     * 保存文章
     * @param githubContentList
     */
    private void saveGithubContentList(List<GithubContent> githubContentList) {
        List<Article> articleList = new ArrayList<>();
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
     * @param keyNodeInfo 节点信息
     * @return
     */
    private Article buildArticle(GithubContent content, KeyNodeInfo keyNodeInfo) {
        Article article = new Article();
        String articleContent = Base64Util.decode(content.getContent());
        String sign = ShaUtil.sign(content.getPath());

        // 解析markdown
        Parser parser = Parser.builder().build();
        Node document = parser.parse(articleContent);

        // 解析标签和介绍、下载图片
        KeyNodeVisitor keyNodeVisitor = new KeyNodeVisitor();
        document.accept(keyNodeVisitor);

        // 从正文中移除标签和介绍
        KeyNodeInfo info = keyNodeVisitor.getKeyNodeInfo();
        if(info != null) {
            // 这里不能直接
            keyNodeInfo.setKeyWord(info.getKeyWord());
            keyNodeInfo.setIntro(info.getIntro());
            // 去除引用标签
            articleContent = articleContent.replaceAll(">\\s+" + keyNodeInfo.getKeyWord(), "");
            articleContent = articleContent.replaceAll(">\\s+" + keyNodeInfo.getIntro(), "");
            // 整理article信息
            int index = content.getName().lastIndexOf(".");
            article.setTitle(content.getName().substring(0, index));
            article.setIntro(keyNodeInfo.getIntro());
            article.setAuthor("depers");
            article.setContent(articleContent);
            article.setSign(sign);
            article.setPath(content.getPath());
            return article;
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
     * @param content
     */
    private void oldestArticleHandler(GithubContent content) {
        /**
         * 如果是老文章
         *  1.更新文章内容
         *  2.插入标签
         *  3.判断标签的结构是否发生变化，若发生则删除原有文章的标签相关的关联，重新插入标签结构
         */
        // 手动事务
        JasperTransactionManager transactionManager = new JasperTransactionManager();
        try {
            KeyNodeInfo keyNodeInfo = new KeyNodeInfo();
            Article article = buildArticle(content, keyNodeInfo);
            if (article == null) {
                return;
            }
            articleMapper.updateSelective(article);

            article = articleMapper.selectBySign(article.getSign());
            // 文章最新的标签
            List<Tag> tags = buildTags(keyNodeInfo.getKeyWord());
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
     * @param content
     */
    private void newestArticleHandler(GithubContent content) {
        KeyNodeInfo keyNodeInfo = new KeyNodeInfo();
        Article article = buildArticle(content, keyNodeInfo);
        if (article == null) {
            return;
        }

        // 手动事务
        JasperTransactionManager transactionManager = new JasperTransactionManager();
        try {
            articleMapper.insertSelective(article);

            List<Tag> tags = buildTags(keyNodeInfo.getKeyWord());

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
     * @param path
     * @return
     * @throws IOException
     */
    private String pullData(String path) throws IOException {
        // 遍历项目目录，解析到最深层文件目录
        // 1.发送请求获取根目录信息
        // 设置超时时间

        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig connConfig = RequestConfig.custom()
                .setConnectTimeout(20000)
                .setSocketTimeout(20000)
                .build();

        HttpGet httpget = new HttpGet(githubConfig.getRepoUrl() + path);
        httpget.addHeader("Authorization", githubConfig.getAccessToken());
        httpget.setConfig(connConfig);

        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                log.error("请求Github报错, url={}, status={}, body={}.", httpget.getURI(), status, EntityUtils.toString(response.getEntity()));
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
        if (content.getType().equals("file") && FileUtils.getFileSuffix(content.getName()).equals(".md")) {
            log.info("拉取文章内容, path={}.", content.getPath());
            String fileJson = pullData(content.getPath());
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

                githubContentList.add(item);
            }
        }
        else if (content.getType().equals("dir")) {
            // 如果不是file就继续往下一层走
            String responseStr = pullData(content.getPath());
            List<GithubContent> list = mapper.readValue(responseStr, List.class);
            for (GithubContent item : list) {
                checkFile(item);
            }
        }
    }

    /**
     * 判断该文件是否在数据库中
     * @param path
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

}
