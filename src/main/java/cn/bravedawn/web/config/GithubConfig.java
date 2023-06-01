package cn.bravedawn.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/18 12:43
 */

@ConfigurationProperties(prefix = "jasper.github")
@Component
public class GithubConfig {

    private String accessToken;
    private String repoUrl;
    private String repoDownloadUrl;
    private String imageStorePath;
    private String assertUrl;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getImageStorePath() {
        return imageStorePath;
    }

    public void setImageStorePath(String imageStorePath) {
        this.imageStorePath = imageStorePath;
    }

    public String getRepoDownloadUrl() {
        return repoDownloadUrl;
    }

    public void setRepoDownloadUrl(String repoDownloadUrl) {
        this.repoDownloadUrl = repoDownloadUrl;
    }

    public String getAssertUrl() {
        return assertUrl;
    }

    public void setAssertUrl(String assertUrl) {
        this.assertUrl = assertUrl;
    }
}
