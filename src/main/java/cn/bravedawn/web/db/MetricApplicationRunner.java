package cn.bravedawn.web.db;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * @author : depers
 * @description :
 * @program : jasper
 * @date : Created in 2023/12/4 22:27
 */

@Component
public class MetricApplicationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MetricApplicationRunner.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).setMetricRegistry(initMetricRegistry("jasper"));
        }
    }

    /**
     * 配置指标监控
     * @param poolName 数据库连接池的名称
     * @return
     */
    public MetricRegistry initMetricRegistry(String poolName) {
        MetricRegistry metricRegistry = new MetricRegistry();
        Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
                .filter((name, metric) -> name.startsWith(poolName + ".pool"))
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(60, TimeUnit.SECONDS);
        return metricRegistry;
    }
}
