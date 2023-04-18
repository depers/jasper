package cn.bravedawn.scheduled;

import cn.bravedawn.scheduled.dto.GithubContent;
import cn.bravedawn.scheduled.dto.KeyNodeInfo;
import cn.bravedawn.web.config.GithubConfig;
import cn.bravedawn.web.db.JasperTransactionManager;
import cn.bravedawn.web.mbg.mapper.ArticleMapper;
import cn.bravedawn.web.mbg.mapper.ArticleTagRelationMapper;
import cn.bravedawn.web.mbg.mapper.TagMapper;
import cn.bravedawn.web.mbg.model.Article;
import cn.bravedawn.web.mbg.model.ArticleTagRelation;
import cn.bravedawn.web.mbg.model.Tag;
import cn.bravedawn.web.util.Base64Util;
import cn.bravedawn.web.util.ShaUtil;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

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

    private static ObjectMapper mapper;
    private static List<GithubContent> githubContentList = new ArrayList<>();
    private static Map<String, KeyNodeInfo> keyNodeInfoMap = new HashMap<>();

    static {
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
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>开始批量拉取Github上的数据。");
            // 拉取数据
            List<GithubContent> list = mapper.readValue(pullData(""), List.class);
            for (GithubContent item : list) {
                checkFile(item);
            }
            // 将文章信息存库
            saveGithubContentList(githubContentList);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>拉取Github上的数据结束。");

        } catch (Throwable e) {
            log.error("定时任务失败，请稍后再试.", e);
        } finally {
            githubContentList.clear();
            keyNodeInfoMap.clear();
        }
    }

    private void saveGithubContentList(List<GithubContent> githubContentList) {
        List<Article> articleList = new ArrayList<>();
        for (GithubContent content : githubContentList) {
            Article article = new Article();
            String articleContent = Base64Util.decode(content.getContent());
            String sign = ShaUtil.sign(content.getPath());
            // 解析markdown
            Parser parser = Parser.builder().build();
            Node document = parser.parse(articleContent);
            // 解析标签和介绍
            KeyNodeVisitor keyNodeVisitor = new KeyNodeVisitor(sign);
            document.accept(keyNodeVisitor);
            // 从正文中移除标签和介绍
            KeyNodeInfo keyNodeInfo = keyNodeInfoMap.get(sign);
            if (keyNodeInfo != null) {
                // 保证事务
                JasperTransactionManager transactionManager = new JasperTransactionManager();
                try {
                    // 去除引用标签
                    articleContent = articleContent.replaceAll("(&gt;|\\>)(.*)", "");

                    // 整理article信息
                    int index = content.getName().indexOf(".");
                    article.setTitle(content.getName().substring(0, index));
                    article.setIntro(keyNodeInfo.getIntro());
                    article.setAuthor("depers");
                    article.setContent(articleContent);
                    article.setSign(sign);
                    article.setPath(content.getPath());
                    articleMapper.insertSelective(article);

                    // 整理tag信息
                    List<String> tagList = Arrays.asList(keyNodeInfo.getKeyWord().split("/"));
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
                    tagMapper.batchInsert(waitTags);
                    maintainTags.addAll(waitTags);

                    // 维护文章和标签的关系表
                    List<ArticleTagRelation> articleTagRelationList = new ArrayList<>();
                    for (Tag tag : maintainTags) {
                        ArticleTagRelation articleTagRelation = new ArticleTagRelation();
                        articleTagRelation.setArticleId(article.getId());
                        articleTagRelation.setTagId(tag.getId());
                        articleTagRelationList.add(articleTagRelation);
                    }
                    articleTagRelationMapper.batchInsert(articleTagRelationList);

                    // 提交事务
                    transactionManager.commit();
                } catch (Throwable e) {
                    // 回滚事务
                    transactionManager.rollback();
                    log.error("批量拉取数据事务回滚", e);
                }
            }
        }
    }


    private String pullData(String path) throws IOException {
        // 遍历项目目录，解析到最深层文件目录
        // 1.发送请求获取根目录信息
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(githubConfig.getRepoUrl() + path);
        httpget.addHeader("Authorization", githubConfig.getAccessToken());

        ResponseHandler< String > responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                log.error("请求Github报错");
                return null;
            }
        };

        String responseBody = httpclient.execute(httpget, responseHandler);
        return responseBody;

    }

    public void checkFile(GithubContent content) throws Exception {
        if (content.getType().equals("file")) {
            log.info("拉取文章内容, path={}.", content.getPath());
            String fileJson = pullData(content.getPath());
            if (StringUtils.isNotBlank(fileJson)) {
                GithubContent item = mapper.readValue(fileJson, GithubContent.class);
                // 判断是否是新文章，若是新文章则添加到githubContentList里，若不是则跳过
                if (!isExistDatabase(item.getPath())) {
                    log.info("[{}]是新文章, path={}.", item.getName(), item.getPath());
                    githubContentList.add(item);
                }
            }
            return;
        }

        // 如果不是file就继续往下一层走
        String responseStr = pullData(content.getPath());
        List<GithubContent> list = mapper.readValue(responseStr, List.class);
        for (GithubContent item : list) {
            checkFile(item);
        }
    }

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
     * 获取引用标签的数据
     */
    class KeyNodeVisitor extends AbstractVisitor {
        private String sign;

        public KeyNodeVisitor(String sign) {
            this.sign = sign;
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            Text keywordNode = (Text)blockQuote.getFirstChild().getFirstChild();
            Text introNode = (Text) blockQuote.getNext().getFirstChild().getFirstChild();
            if (keywordNode != null && introNode != null) {
                keyNodeInfoMap.put(sign, new KeyNodeInfo(keywordNode.getLiteral(), introNode.getLiteral()));
            }
        }
    }

}
