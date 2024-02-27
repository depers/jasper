package cn.bravedawn.scheduled.analysis;

import cn.bravedawn.scheduled.properties.GiteeConfig;
import cn.bravedawn.scheduled.constant.Constants;
import cn.bravedawn.scheduled.dto.GiteeContent;
import cn.bravedawn.scheduled.dto.GithubContent;
import cn.bravedawn.scheduled.serializer.GiteeCustomDeserializer;
import cn.bravedawn.scheduled.serializer.GiteeCustomFileDeserializer;
import cn.bravedawn.web.util.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/21 11:44
 */

@Component
public class GiteePullData extends PullData {

    private static final Logger log = LoggerFactory.getLogger(GiteePullData.class);

    // 保存批量执行过程中的文章信息，避免批量重复执行
    private static ThreadLocal<List<GiteeContent>> giteeContentListThreadLocal = new ThreadLocal<>();

    @Autowired
    private GiteeConfig giteeConfig;

    private static final ObjectMapper mapper;


    static {
        // 配置自定义的反序列化器
        mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("GiteeCustomDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(List.class, new GiteeCustomDeserializer());
        module.addDeserializer(GiteeContent.class, new GiteeCustomFileDeserializer());
        mapper.registerModule(module);
    }

    @Override
    public List<GiteeContent> loadData() {

        try {
            // 初始化本地线程对象
            giteeContentListThreadLocal.set(new ArrayList<>());

            String masterContentStr = pullData(giteeConfig.getRepoUrl() + "master");
            if (StringUtils.isBlank(masterContentStr)) {
                log.error("获取主页数据为空");
                return null;
            }
            List<GithubContent> list = mapper.readValue(masterContentStr, List.class);
            for (GithubContent item : list) {
                StringBuilder path = new StringBuilder();
                checkFile(item, path);
            }
            return giteeContentListThreadLocal.get();
        } catch (Throwable e) {
            log.error("拉取数据失败", e);
        } finally {
            giteeContentListThreadLocal.remove();
        }
        return null;
    }


    /**
     * 请求gitee拉取数据
     */
    public String pullData(String url) {
        try {
            // 设置超时时间
            CloseableHttpClient httpClient = HttpClients.createDefault();
            RequestConfig connConfig = RequestConfig.custom()
                    .setConnectTimeout(20000)
                    .setSocketTimeout(20000)
                    .build();

            HttpGet httpget = new HttpGet(url);
            URI uri = new URIBuilder(httpget.getURI())
                    .addParameter("access_token", giteeConfig.getAssessToken())
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
        } catch (Throwable e) {
            log.error("请求Gitee出现异常", e);
        }
        return null;
    }




    private void checkFile(GithubContent item, StringBuilder path) throws JsonProcessingException {

        if (StringUtils.equals(item.getType(), Constants.GITEE_TYPE_TREE)) {
            String itemResult = pullData(giteeConfig.getRepoUrl() + item.getSha());
            List<GithubContent> list = mapper.readValue(itemResult, List.class);
            path.append(item.getPath() + "/");
            for (GithubContent i : list) {
                checkFile(i, path);
            }
        } else if (StringUtils.equals(item.getType(), Constants.GITEE_TYPE_BLOB)
                    && FileUtils.getFileSuffix(item.getName()).equals(".md")) {
            String itemResult = pullData(giteeConfig.getBlobUrl() + item.getSha());
            GiteeContent giteeContent = mapper.readValue(itemResult, GiteeContent.class);
            giteeContent.setName(item.getName());
            giteeContent.setPath(path.append(item.getName()).toString());
            giteeContent.setType(Constants.GITEE_TYPE_BLOB);
            // 判断是否是新文章，若是新文章则添加到githubContentList里，若不是则跳过
            if (!isExistDatabase(item.getPath())) {
                log.info("[{}]是新文章, path={}.", item.getName(), item.getPath());
                item.setExist(false);
            } else {
                log.info("[{}]是老文章, path={}.", item.getName(), item.getPath());
                item.setExist(true);
            }
            giteeContentListThreadLocal.get().add(giteeContent);
        }
    }
}
