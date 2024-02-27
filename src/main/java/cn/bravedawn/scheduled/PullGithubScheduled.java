package cn.bravedawn.scheduled;

import cn.bravedawn.scheduled.analysis.PullData;
import cn.bravedawn.scheduled.analysis.PullDataFactory;
import cn.bravedawn.scheduled.constant.PullDataRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
