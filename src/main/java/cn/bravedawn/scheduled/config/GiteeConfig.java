package cn.bravedawn.scheduled.config;

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
}


