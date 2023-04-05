package cn.bravedawn.web.dto.detail;

import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/5 14:24
 */
public class ArticleDetailDTO {

    private List<String> tags;
    private String title;
    private String publishDate;
    private String content;
    private List<CommentDTO> commentList;


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<CommentDTO> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentDTO> commentList) {
        this.commentList = commentList;
    }
}
