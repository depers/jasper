package cn.bravedawn.scheduled;

import cn.bravedawn.scheduled.config.GithubConfig;
import cn.bravedawn.scheduled.constant.Constants;
import cn.bravedawn.scheduled.dto.GithubBlob;
import cn.bravedawn.scheduled.dto.GithubBlobContent;
import cn.bravedawn.scheduled.serializer.GithubBlobDeserializer;
import cn.bravedawn.scheduled.serializer.GithubBlobFileDeserializer;
import cn.bravedawn.web.util.HttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/24 16:46
 */

@Component
public class PullAssertImageScheduled {

    private static final Logger log = LoggerFactory.getLogger(PullAssertImageScheduled.class);

    private static final ObjectMapper mapper;

    private static final Map<String, String> headers = new HashMap<>();

    // 记录文件是否已经保存
    private static final Map<String, Boolean> LOCAL_CACHE = new ConcurrentHashMap<>();


    @Autowired
    private GithubConfig githubConfig;

    @Value("${jasper.pullImgage.path}")
    private String pullImagePath;

    // 保存批量执行过程中的文章信息，避免批量重复执行
    private static ThreadLocal<List<GithubBlobContent>> githubBlobList = new ThreadLocal<>();


    static {
        // 配置自定义的反序列化器
        mapper = new ObjectMapper();
        SimpleModule module =
                new SimpleModule("CustomDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(List.class, new GithubBlobDeserializer());
        module.addDeserializer(GithubBlobContent.class, new GithubBlobFileDeserializer());
        mapper.registerModule(module);
    }

    @PostConstruct
    private void setup() {
        // 添加头信息
        headers.put("Authorization", githubConfig.getAccessToken());
    }

    // @Scheduled(cron = "0 0/1 * * * ? ")
    private void pullImages() {

        try {
            // 配置批量日志打印关键字
            MDC.put("appId", "jasper");
            MDC.put("tradeName", "JOB-PullAssertImageScheduled");
            MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));
            githubBlobList.set(new ArrayList<>());
            log.info("----------------------开始批量拉取远程仓库上的图片");
            pullData();
            saveImages();
            log.info("----------------------批量拉取远程仓库上的图片结束");
        } catch (Throwable e) {
            log.error("定时任务失败，请稍后再试.", e);
        } finally {
            MDC.clear();
            githubBlobList.remove();
        }
    }


    private void pullData() throws JsonProcessingException {
        String assertUrl = githubConfig.getRepoUrl() + "assert";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", githubConfig.getAccessToken());
        String respString = HttpClient.get(assertUrl, headers);

        if (StringUtils.isBlank(respString)) {
            log.error("获取assert数据为空");
            return;
        }

        List<GithubBlob> githubBlobs = mapper.readValue(respString, List.class);
        for (GithubBlob blob : githubBlobs) {
            checkData(blob);
        }


    }

    private void checkData(GithubBlob blob) throws JsonProcessingException {
        if (blob.getType().equals(Constants.GITHUB_BLOB_TYPE_DIR)) {
            log.info("开始解析文件夹, dir={}", blob.getPath());
            String dirUrl = githubConfig.getRepoUrl() + blob.getPath();
            String respString = HttpClient.get(dirUrl, headers);

            if (StringUtils.isBlank(respString)) {
                log.error("获取dir数据为空");
                return;
            }

            List<GithubBlob> blobs = mapper.readValue(respString, List.class);
            for (GithubBlob item : blobs) {
                checkData(item);
            }
        } else if (blob.getType().equals(Constants.GITHUB_BLOB_TYPE_FILE)) {
            // 校验图片是否已经保存到了缓存中
            if (LOCAL_CACHE.containsKey(blob.getSha())) {
                log.info("该图片已经完成下载，不再处理。imageName={}", blob.getPath());
                return;
            }
            log.info("开始解析文件, file={}", blob.getPath());
            String resultStr = HttpClient.get(blob.getGitUrl(), headers);
            if (StringUtils.isBlank(resultStr)) {
                log.error("获取dir数据为空");
                return;
            }

            GithubBlobContent githubBlobContent = mapper.readValue(resultStr, GithubBlobContent.class);
            githubBlobContent.setName(blob.getName());
            githubBlobContent.setPath(blob.getPath());
            githubBlobList.get().add(githubBlobContent);
        }
    }

    private void saveImages() {
        List<GithubBlobContent> contentList = githubBlobList.get();
        for (GithubBlobContent content : contentList) {
            log.info("将文件保存到本地, filePath={}", content.getPath());
            String path = pullImagePath + content.getPath();
            try {
                Path parentPath = Paths.get(path).getParent();
                if (!Files.exists(parentPath)) {
                    Files.createDirectories(parentPath);
                }
                Files.write(Paths.get(path), Base64.getDecoder().decode(content.getContent().replace("\n", "")));
                LOCAL_CACHE.putIfAbsent(content.getSha(), true);
            } catch (Throwable e) {
                log.error("写文件失败", e);
            }

        }
    }
}
