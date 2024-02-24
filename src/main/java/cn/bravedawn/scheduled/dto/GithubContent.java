package cn.bravedawn.scheduled.dto;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/2/2 16:13
 */
public class GithubContent extends Content{

    /**
     * 反序列化列表的时候使用
     */
    public GithubContent(String name, String path, String type, String url) {
        super(name, path, type, null, null, url, false);
    }

    /**
     * 反序列化对象的时候使用
     */
    public GithubContent(String name, String path, String type, String content, String sha) {
        super(name, path, type, content, sha, null, false);
    }

    @Override
    public String toString() {
        return "GithubContent{" + super.toString() + "} ";
    }
}
