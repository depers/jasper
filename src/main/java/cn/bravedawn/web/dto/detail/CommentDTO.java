package cn.bravedawn.web.dto.detail;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/5 14:53
 */
public class CommentDTO {

    private String nickname;
    private String comment;
    private String commentDate;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }
}
