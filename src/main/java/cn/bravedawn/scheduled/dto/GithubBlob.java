package cn.bravedawn.scheduled.dto;

/**
 * @author : depers
 * @program : jasper
 * @date : Created in 2024/2/24 17:03
 */
public class GithubBlob {

    /**
     * 文件夹
     * {
     * "name": "2023",
     * "path": "assert/2023",
     * "sha": "7e8d238b8875d9e82a7aab17c8d681e59021ac88",
     * "size": 0,
     * "url": "https://api.github.com/repos/depers/jasper-db/contents/assert/2023?ref=main",
     * "html_url": "https://github.com/depers/jasper-db/tree/main/assert/2023",
     * "git_url": "https://api.github.com/repos/depers/jasper-db/git/trees/7e8d238b8875d9e82a7aab17c8d681e59021ac88",
     * "download_url": null,
     * "type": "dir",
     * "_links":{
     * "self": "https://api.github.com/repos/depers/jasper-db/contents/assert/2023?ref=main",
     * "git": "https://api.github.com/repos/depers/jasper-db/git/trees/7e8d238b8875d9e82a7aab17c8d681e59021ac88",
     * "html": "https://github.com/depers/jasper-db/tree/main/assert/2023"
     * }
     * },
     *
     * 文件
     * {
     * "name": "InputStream.png",
     * "path": "assert/InputStream.png",
     * "sha": "4269be1f7d92740742378e5d9cc46a2209eb5624",
     * "size": 51464,
     * "url": "https://api.github.com/repos/depers/jasper-db/contents/assert/InputStream.png?ref=main",
     * "html_url": "https://github.com/depers/jasper-db/blob/main/assert/InputStream.png",
     * "git_url": "https://api.github.com/repos/depers/jasper-db/git/blobs/4269be1f7d92740742378e5d9cc46a2209eb5624",
     * "download_url": "https://raw.githubusercontent.com/depers/jasper-db/main/assert/InputStream.png",
     * "type": "file",
     * "_links":{
     * "self": "https://api.github.com/repos/depers/jasper-db/contents/assert/InputStream.png?ref=main",
     * "git": "https://api.github.com/repos/depers/jasper-db/git/blobs/4269be1f7d92740742378e5d9cc46a2209eb5624",
     * "html": "https://github.com/depers/jasper-db/blob/main/assert/InputStream.png"
     * }
     * }
     */

    private String name;
    private String path;
    private String gitUrl;
    private String type;
    private String sha;

    public GithubBlob(String name, String path, String gitUrl, String type, String sha) {
        this.name = name;
        this.path = path;
        this.gitUrl = gitUrl;
        this.type = type;
        this.sha = sha;
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

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    @Override
    public String toString() {
        return "GithubBlob{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", gitUrl='" + gitUrl + '\'' +
                ", type='" + type + '\'' +
                ", sha='" + sha + '\'' +
                '}';
    }
}
