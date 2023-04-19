package cn.bravedawn.web.mbg.mapper;

import cn.bravedawn.web.mbg.model.Article;

import java.util.List;

public interface ArticleMapper {

    List<Article> selectArticleList();

    Article selectById(long articleId);

    int selectCountByMd5(String sha);

    int insertSelective(Article article);

    int updateSelective(Article article);

    Article selectBySign(String sign);
}