package cn.bravedawn.scheduled.analysis;

import cn.bravedawn.scheduled.serializer.GithubCustomDeserializer;
import cn.bravedawn.scheduled.serializer.GithubCustomFileDeserializer;
import cn.bravedawn.scheduled.dto.GithubContent;
import cn.bravedawn.scheduled.properties.GithubConfig;
import cn.bravedawn.web.util.FileUtils;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/21 11:44
 */

@Component
public class GithubPullData extends PullData {

    private static final Logger log = LoggerFactory.getLogger(GithubPullData.class);

    private static final ObjectMapper mapper;

    @Autowired
    private GithubConfig githubConfig;


    // 保存批量执行过程中的文章信息，避免批量重复执行
    private static ThreadLocal<List<GithubContent>> githubContentListThreadLocal = new ThreadLocal<>();

    static {
        // 配置自定义的反序列化器
        mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(List.class, new GithubCustomDeserializer());
        module.addDeserializer(GithubContent.class, new GithubCustomFileDeserializer());
        mapper.registerModule(module);
    }

    @Override
    public List<GithubContent> loadData() {
        try {
            githubContentListThreadLocal.set(new ArrayList<>());
            // 拉取数据
            List<GithubContent> list = mapper.readValue(pullData("", false), List.class);
            for (GithubContent item : list) {
                checkFile(item);
            }
            return githubContentListThreadLocal.get();
        } catch (Throwable e) {
            log.error("拉取数据出现异常", e);
        } finally {
            githubContentListThreadLocal.remove();
        }
        return null;
    }


    /**
     * 校验文章是新文章还是老文件
     * @param content
     * @throws Exception
     */
    public void checkFile(GithubContent content) throws Exception {

        List<GithubContent> githubContents = new ArrayList<>();

        if (content.getType().equals("file") && FileUtils.getFileSuffix(content.getName()).equals(".md")) {
            log.info("拉取文章内容, path={}.", content.getPath());
            String fileJson = pullData(content.getUrl(), true);
            if (StringUtils.isNotBlank(fileJson)) {
                GithubContent item = mapper.readValue(fileJson, GithubContent.class);
                // 判断是否是新文章，若是新文章则添加到githubContentList里，若不是则跳过
                if (!isExistDatabase(item.getPath())) {
                    log.info("[{}]是新文章, path={}.", item.getName(), item.getPath());
                    item.setExist(false);
                } else {
                    log.info("[{}]是老文章, path={}.", item.getName(), item.getPath());
                    item.setExist(true);
                }
                githubContentListThreadLocal.get().add(item);
            }
        } else if (content.getType().equals("dir")) {
            // 如果不是file就继续往下一层走
            String responseStr = pullData(content.getPath(), false);
            List<GithubContent> list = mapper.readValue(responseStr, List.class);
            for (GithubContent item : list) {
                checkFile(item);
            }
        }
    }


    /**
     * 从github拉取数据
     * @param path 请求地址
     * @param isDirect 是否为直接请求的地址，如果是目录则需要加签名的前缀，如果是文章则为直接请求的目录
     * @return
     * @throws IOException
     */
    private String pullData(String path, boolean isDirect) throws IOException {
        // 遍历项目目录，解析到最深层文件目录
        // 1.发送请求获取根目录信息
        // 设置超时时间
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig connConfig = RequestConfig.custom()
                .setConnectTimeout(20000)
                .setSocketTimeout(20000)
                .build();

        String url = isDirect ? path : githubConfig.getRepoUrl() + path;
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Authorization", githubConfig.getAccessToken());
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

        String responseBody = httpClient.execute(httpget, responseHandler);
        return responseBody;
    }


}
