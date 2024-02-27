package cn.bravedawn.scheduled.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/21 15:41
 */
@ConfigurationProperties(prefix = "jasper.gitee")
@Component
public class GiteeConfig {

    private String assessToken;
    private String repoUrl;
    private String blobUrl;

    public String getAssessToken() {
        return assessToken;
    }

    public void setAssessToken(String assessToken) {
        this.assessToken = assessToken;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getBlobUrl() {
        return blobUrl;
    }

    public void setBlobUrl(String blobUrl) {
        this.blobUrl = blobUrl;
    }
}


