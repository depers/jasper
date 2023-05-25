package cn.bravedawn.web.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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

        try {
            HttpClient httpclient = createTlsV2HttpClient();
            HttpGet httpget = new HttpGet(url);

            ResponseHandler<InputStream> responseHandler = response -> {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return entity.getContent();
                }
                return null;
            };

            return httpclient.execute(httpget, responseHandler);
        } catch (Throwable e) {
            log.error("下载文件失败", e);
        }
        return null;

    }


    public static HttpClient createTlsV2HttpClient() throws KeyManagementException,
            UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(new File("/Users/depers/Desktop/my.keystore"),
                        "changeit".toCharArray(),
                        new TrustSelfSignedStrategy())
                .build();

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        return httpclient;
    }


    public static void main(String[] args) {
        downloadFile("https://raw.githubusercontent.com/depers/jasper-front/main/static/image/jasper2.0.jpeg");
    }



}
