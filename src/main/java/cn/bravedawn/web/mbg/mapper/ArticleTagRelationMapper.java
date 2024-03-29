package cn.bravedawn.web.mbg.mapper;

import cn.bravedawn.web.mbg.model.ArticleTagRelation;

import java.util.List;

public interface ArticleTagRelationMapper {

    int batchInsert(List<ArticleTagRelation> list);

    List<String> selectTagNameByArticleId(long articleId);

    int deleteByArticle(long articleId);

}