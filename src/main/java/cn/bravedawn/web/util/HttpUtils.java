package cn.bravedawn.web.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/5/24 21:56
 */
public class HttpUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);


    /**
     * 下载文件
     * @param url 文件地址
     * @return
     */
    public static InputStream downloadFile(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);

        ResponseHandler<InputStream> responseHandler = response -> {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
            return null;
        };

        try {
            return httpclient.execute(httpget, responseHandler);
        } catch (Throwable e) {
            log.error("下载文件失败", e);
        }
        return null;

    }
}
