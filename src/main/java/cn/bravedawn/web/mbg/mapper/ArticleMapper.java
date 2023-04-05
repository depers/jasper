package cn.bravedawn.web.mbg.mapper;

import cn.bravedawn.web.mbg.model.Article;

import java.util.List;

public interface ArticleMapper {

    List<Article> selectArticleList();

    Article selectById(long articleId);
}