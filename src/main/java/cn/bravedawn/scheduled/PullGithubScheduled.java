package cn.bravedawn.scheduled;

import cn.bravedawn.scheduled.dto.GithubContent;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/2/1 15:50
 */
public class PullGithubScheduled {


    private static ObjectMapper mapper;
    private static List<GithubContent> githubContentList = new ArrayList<>();

    static {
        mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(List.class, new CustomDeserializer());
        mapper.registerModule(module);
    }

    private final static String base_url = "https://api.github.com/repos/depers/jasper-db/contents/";


    public void runTask() {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // 执行逻辑
            }
        }, 1, 3, TimeUnit.SECONDS);
    }


    private static String pullData(String path) throws IOException {
        System.out.println("请求path=" + path);
        // 遍历项目目录，解析到最深层文件目录
        // 1.发送请求获取根目录信息
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(base_url + path);
        httpget.addHeader("Authorization", "Bearer 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");

        ResponseHandler< String > responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };

        String responseBody = httpclient.execute(httpget, responseHandler);
        return responseBody;
        // 将文件信息落库

    }

    public static boolean checkFile(GithubContent content) throws Exception {
        System.out.println("1--content=" + content);
        if (content.getType().equals("file")) {
            githubContentList.add(content);
            return true;
        }

        // 如果不是file就继续往下一层走
        String responseStr = pullData(content.getPath());
        List<GithubContent> list = mapper.readValue(responseStr, List.class);
        for (GithubContent item : list) {
            return checkFile(item);
        }

        return false;
    }


    public static void main(String[] args) throws Exception {
        List<GithubContent> list = mapper.readValue(pullData(""), List.class);
        for (GithubContent item : list) {
            checkFile(item);
        }


        System.out.println(githubContentList);
    }




}
