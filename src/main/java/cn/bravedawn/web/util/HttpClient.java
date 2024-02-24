package cn.bravedawn.web.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/24 16:57
 */
public class HttpClient {

    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);


    /**
     * 发送Http get请求
     * @param url
     * @param headers
     * @return
     * @throws IOException
     */
    public static String get(String url, Map<String, String> headers) {
        // 遍历项目目录，解析到最深层文件目录
        // 1.发送请求获取根目录信息
        // 设置超时时间
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig connConfig = RequestConfig.custom()
                .setConnectTimeout(20000)
                .setSocketTimeout(20000)
                .build();

        HttpGet httpget = new HttpGet(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpget.addHeader(entry.getKey(), entry.getValue());
        }
        httpget.setConfig(connConfig);

        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                log.error("请求Github报错, url={}, status={}, body={}.", httpget.getURI(), status,
                        EntityUtils.toString(response.getEntity()));
                return null;
            }
        };
        String respString = null;
        try {
            respString = httpClient.execute(httpget, responseHandler);
        } catch (Throwable e) {
            log.error("请求数据失败", e);
        }
        return respString;
    }


}
