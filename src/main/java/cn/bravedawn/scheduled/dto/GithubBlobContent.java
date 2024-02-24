package cn.bravedawn.scheduled.dto;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/24 17:29
 */
public class GithubBlobContent {

    private String name;
    private String path;
    private String sha;
    private int size;
    private String content;
    private String encoding;

    public GithubBlobContent(String sha, int size, String content, String encoding) {
        this.sha = sha;
        this.size = size;
        this.content = content;
        this.encoding = encoding;
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

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String toString() {
        return "GithubBlobContent{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", sha='" + sha + '\'' +
                ", size=" + size +
                ", content='" + content + '\'' +
                ", encoding='" + encoding + '\'' +
                '}';
    }
}
