package cn.bravedawn.web.mbg.mapper;

import cn.bravedawn.web.mbg.model.Comment;

import java.util.List;

public interface CommentMapper {

    List<Comment> selectListByArticleId(long articleId);
}