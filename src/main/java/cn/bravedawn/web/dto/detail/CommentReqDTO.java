package cn.bravedawn.web.dto.detail;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/5 15:09
 */
public class CommentReqDTO {

    private String articleId;
    private String nickname;
    private String content;
    private String email;
    private String personalSite;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonalSite() {
        return personalSite;
    }

    public void setPersonalSite(String personalSite) {
        this.personalSite = personalSite;
    }
}
