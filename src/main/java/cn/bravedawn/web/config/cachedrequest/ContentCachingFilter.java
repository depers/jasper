package cn.bravedawn.web.config.cachedrequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/8 17:44
 */

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "ContentCachingFilter", urlPatterns = "/*")
public class ContentCachingFilter extends OncePerRequestFilter {


    /**
     * 这个Filter主要有两个功能
     * 1. 打印请求和响应参数
     * 2. 统计请求的处理时间
     * 3. 日志MDC
     */


    private static final Logger log = LoggerFactory.getLogger(ContentCachingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        long startTime = System.currentTimeMillis();

        CachedBodyHttpServletRequest req = new CachedBodyHttpServletRequest(request);
        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(response);

        String requestBodyStr = "";
        if (req.getMethod().equals(HttpMethod.GET.name())) {
            ObjectMapper mapper = new ObjectMapper();
            requestBodyStr = mapper.writeValueAsString(req.getParameterMap());
        } else {
            byte[] requestBody = StreamUtils.copyToByteArray(req.getInputStream());
            requestBodyStr = new String(requestBody, StandardCharsets.UTF_8).replaceAll("\r?\n", "");
        }


        // 请求前的日志打印
        MDC.put("appId", "jasper");
        MDC.put("tradeName", req.getRequestURI() + "-" + req.getMethod());
        MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));
        log.info("请求:{}, start request.", req.getRequestURI());
        log.info("请求参数:{}.", requestBodyStr);

        filterChain.doFilter(req, resp);

        // 请求后的日志打印
        byte[] responseBody = resp.getContentAsByteArray();
        log.info("响应参数:{}.", new String(responseBody, StandardCharsets.UTF_8));
        long costTime = System.currentTimeMillis() - startTime;
        log.info("请求:{}, cost end time is [{}]ms.", req.getRequestURI(), costTime);
        resp.copyBodyToResponse();
    }
}
