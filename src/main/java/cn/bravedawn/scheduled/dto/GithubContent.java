package cn.bravedawn.scheduled.dto;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/2/2 16:13
 */
public class GithubContent {

    private String name;
    private String path;
    private String type;
    private String content;
    private String sha;
    private String url;

    /**
     * 是否已经存在，若存在则更新文章内容
     */
    private boolean isExist;

    public GithubContent(String name, String path, String type, String url) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.url = url;
    }

    public GithubContent(String name, String path, String type, String content, String sha) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.content = content;
        this.sha = sha;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsExist() {
        return isExist;
    }

    public void setIsExist(boolean exist) {
        isExist = exist;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "GithubContent{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", sha='" + sha + '\'' +
                ", url='" + url + '\'' +
                ", isExist=" + isExist +
                '}';
    }
}
