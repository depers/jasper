package cn.bravedawn.jasper;

import cn.bravedawn.web.JasperApplication;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @Author : fengx9
 * @Project : jasper-2.1
 * @Date : Created in 2023-11-28 10:44
 */


@SpringBootTest(classes = JasperApplication.class)
@ExtendWith(SpringExtension.class)
public class HikariCPTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testGetConnection() throws SQLException {
        System.out.println(DataSourceUtils.getConnection(dataSource));

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        System.out.println(hikariDataSource);
    }

}
