package cn.bravedawn.web.db;

import cn.bravedawn.web.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/5 15:41
 */
public class JasperTransactionManager {

    private static final Logger log = LoggerFactory.getLogger(JasperTransactionManager.class);

    private TransactionStatus transactionStatus;

    private final DataSourceTransactionManager transactionManager = (DataSourceTransactionManager) SpringContextUtil.getBean("transactionManager");

    public JasperTransactionManager() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setTimeout(60);
        this.transactionStatus = transactionManager.getTransaction(def);
    }

    public void commit() {
        transactionManager.commit(transactionStatus);
    }


    public void rollback() {
        transactionManager.rollback(transactionStatus);
    }

}
