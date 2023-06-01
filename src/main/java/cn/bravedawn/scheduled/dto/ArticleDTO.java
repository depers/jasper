package cn.bravedawn.scheduled.dto;

import cn.bravedawn.web.mbg.model.Article;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/6/1 15:33
 */
public class ArticleDTO {

    private Article article;
    private String tag;

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
