package cn.bravedawn.web;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.bravedawn.web.mbg.mapper")
public class JasperApplication {

    public static void main(String[] args) {
        SpringApplication.run(JasperApplication.class, args);
    }

}
