package cn.bravedawn.web.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Article {

    /**
     * 主键
     */
    private long id;
    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章简介
     */
    private String intro;
    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章路径
     */
    private String path;

    /**
     * 作者
     */
    private String author;

    /**
     * 发布日期
     */
    private LocalDate publishDate;

    /**
     * 创建时间
     */
    private LocalDateTime insertTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
