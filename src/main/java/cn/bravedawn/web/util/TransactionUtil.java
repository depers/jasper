package cn.bravedawn.web.util;

import cn.bravedawn.scheduled.PullGithubScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Date;

/**
 * @Author : fengx9
 * @Project : jasper-2.1
 * @Date : Created in 2023-11-28 15:50
 */

public class TransactionUtil {

    private static final Logger log = LoggerFactory.getLogger(TransactionUtil.class);

    private static DataSource dataSource = (DataSource) SpringContextUtil.getBean(DataSource.class);

    /**
     * 打印事务的死亡时间
     */
    public static void log(String logPrefix) {
        ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
        Date deadline = holder.getDeadline();
        log.info("{}-当前事务的死亡时间：{}", logPrefix, LocalDateUtil.toLocalDateTimeStr(deadline));
    }
}
