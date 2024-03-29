package cn.bravedawn.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
public class CorsConfig {


    @Bean
    public CorsFilter corsFilter() {
        // 1. 添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://www.bravedawn.cn");
        config.addAllowedOrigin("http://43.142.173.83");

        // 设置是否发送cookie信息
        config.setAllowCredentials(true);

        // 设置允许请求的方式
        config.addAllowedMethod("*");

        // 设置允许的header
        config.addAllowedHeader("*");

        // 2. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        // 为所有的请求路径设置跨域配置
        corsSource.registerCorsConfiguration("/**", config);

        // 3. 返回重新定义好的corsSource
        return new CorsFilter(corsSource);
    }
}
