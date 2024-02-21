package cn.bravedawn.scheduled.analysis;

import cn.bravedawn.scheduled.config.GiteeConfig;
import cn.bravedawn.scheduled.dto.GithubContent;
import cn.bravedawn.scheduled.serializer.GiteeCustomDeserializer;
import cn.bravedawn.scheduled.serializer.GithubCustomDeserializer;
import cn.bravedawn.scheduled.serializer.GithubCustomFileDeserializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/21 11:44
 */

@Component
public class GiteePullData implements PullData {

    private static final Logger log = LoggerFactory.getLogger(GiteePullData.class);

    @Autowired
    private GiteeConfig giteeConfig;

    private static final ObjectMapper mapper;


    static {
        // 配置自定义的反序列化器
        mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("GiteeCustomDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(List.class, new GiteeCustomDeserializer());
        module.addDeserializer(GithubContent.class, new GithubCustomFileDeserializer());
        mapper.registerModule(module);
    }

    @Override
    public List<GithubContent> loadData() {

        try {
            pullData("master");
        } catch (Throwable e) {
            log.error("");
        }


        return null;
    }


    private String pullData(String sha) throws URISyntaxException, IOException {
        // 设置超时时间
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig connConfig = RequestConfig.custom()
                .setConnectTimeout(20000)
                .setSocketTimeout(20000)
                .build();

        String url = giteeConfig.getRepoUrl();
        HttpGet httpget = new HttpGet(url);
        URI uri = new URIBuilder(httpget.getURI())
                .addParameter("access_token", giteeConfig.getAssessToken())
                .addParameter("owner", "depers")
                .addParameter("repo", "jasper-db")
                .addParameter("sha", sha)
                .build();

        httpget.setURI(uri);
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

        return httpClient.execute(httpget, responseHandler);
    }


    private void checkFile() {

    }
}
