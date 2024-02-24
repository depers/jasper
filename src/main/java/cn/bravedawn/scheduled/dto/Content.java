package cn.bravedawn.scheduled.dto;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/23 20:51
 */
public class Content {

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

    public Content(String name, String path, String type, String content, String sha, String url, boolean isExist) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.content = content;
        this.sha = sha;
        this.url = url;
        this.isExist = isExist;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }


    @Override
    public String toString() {
        return "Content{" +
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
