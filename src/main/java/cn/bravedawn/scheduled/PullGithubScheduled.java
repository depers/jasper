package cn.bravedawn.scheduled;

import cn.bravedawn.scheduled.analysis.GiteePullData;
import cn.bravedawn.scheduled.analysis.PullData;
import cn.bravedawn.scheduled.analysis.PullDataFactory;
import cn.bravedawn.scheduled.constant.PullDataRepos;
import cn.bravedawn.scheduled.dto.ArticleDTO;
import cn.bravedawn.scheduled.dto.GithubContent;
import cn.bravedawn.scheduled.markdown.KeyNodeVisitor;
import cn.bravedawn.scheduled.config.GithubConfig;
import cn.bravedawn.scheduled.serializer.GithubCustomDeserializer;
import cn.bravedawn.scheduled.serializer.GithubCustomFileDeserializer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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


    @Value("${jasper.pullData.repo}")
    private String pullDataRepo;

    @Autowired
    private PullDataFactory pullDataFactory;


    /**
     * 这段批量的逻辑简单来叙述一下：
     * 1. 拉取github上面的数据，并逐步递归迭代，获取到所有的文章信息
     * 2. 保存文章
     * 1）通过文章路径的md5，判断是否为新文章，若为新文章则直接插入数据库，并维护标签和文章的关系
     * 2) 判断是否需要更新文章，这里根据github返回的sign字段来进行判断，若更新了文章，则会更新文章到数据库，并维护标签和文章的关系
     */
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void runTask() {
        try {
            // 配置批量日志打印关键字
            MDC.put("appId", "jasper");
            MDC.put("tradeName", "JOB-PullGithubScheduled");
            MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));

            log.info("----------------------开始批量拉取远程仓库上的数据");
            if (pullDataRepo.equals(PullDataRepos.GITHUB_REPO)) {
                log.info("采用Github作为数据源");
            } else {
                log.info("采用Gitee作为数据源");
            }
            PullData strategy = pullDataFactory.getStrategy(pullDataRepo);
            strategy.storeArticle();
            log.info("----------------------批量拉取远程仓库上的数据结束");
        } catch (Throwable e) {
            log.error("定时任务失败，请稍后再试.", e);
        } finally {
            MDC.clear();
        }
    }


}
