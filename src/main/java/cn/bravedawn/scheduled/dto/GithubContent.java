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

    public GithubContent(String name, String path, String type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public GithubContent(String name, String path, String type, String content) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.content = content;
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

    @Override
    public String toString() {
        return "GithubContent{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
