package cn.bravedawn.scheduled.dto;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/2/2 16:13
 */
public class GiteeContent extends Content{

    private int size;
    private String encoding;


    public GiteeContent(String name, String path, String type, String url) {
        super(name, path, type, null, null, url, false);
    }

    public GiteeContent(String sha, String content, int size, String url, String encoding) {
        super(null, null, null, content, sha, url, false);
        this.size = size;
        this.encoding = encoding;
    }

    @Override
    public String toString() {
        return "GiteeContent{" +
                "size=" + size +
                ", encoding='" + encoding + '\'' +
                "} " + super.toString();
    }
}
