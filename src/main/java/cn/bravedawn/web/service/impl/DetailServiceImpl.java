package cn.bravedawn.web.service.impl;

import cn.bravedawn.web.common.CommonResult;
import cn.bravedawn.web.common.ResultEnum;
import cn.bravedawn.web.db.JasperTransactionManager;
import cn.bravedawn.web.dto.detail.ArticleDetailDTO;
import cn.bravedawn.web.dto.detail.CommentDTO;
import cn.bravedawn.web.dto.detail.CommentReqDTO;
import cn.bravedawn.web.exception.BusinessException;
import cn.bravedawn.web.mbg.mapper.ArticleMapper;
import cn.bravedawn.web.mbg.mapper.CommentMapper;
import cn.bravedawn.web.mbg.mapper.TagMapper;
import cn.bravedawn.web.mbg.model.Article;
import cn.bravedawn.web.mbg.model.Comment;
import cn.bravedawn.web.service.DetailService;
import cn.bravedawn.web.util.LocalDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/5 14:25
 */

@Service
public class DetailServiceImpl implements DetailService {

    private static final Logger log = LoggerFactory.getLogger(DetailServiceImpl.class);

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TagMapper tagMapper;


    @Override
    public CommonResult<ArticleDetailDTO> getDetail(long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            log.error("没有找到id={}的文章，查询失败", articleId);
            throw new BusinessException(ResultEnum.ARTICLE_NO_EXIST_ERROR);
        }

        List<Comment> commentList = commentMapper.selectListByArticleId(articleId);
        List<CommentDTO> commentDTOList = new ArrayList<>();
        commentList.forEach(comment -> {
            CommentDTO cm = new CommentDTO();
            cm.setNickname(comment.getNickname());
            cm.setComment(comment.getContent());
            cm.setCommentDate(LocalDateUtil.toLocalDateTimeStr(comment.getInsertTime()));
            commentDTOList.add(cm);
        });

        ArticleDetailDTO detailDTO = new ArticleDetailDTO();

        List<String> tagList = tagMapper.selectTagNameByArticleId(articleId);
        detailDTO.setTags(tagList);
        detailDTO.setTitle(article.getTitle());
        detailDTO.setPublishDate(LocalDateUtil.toLocalDateTimeStr(article.getInsertTime()));
        detailDTO.setContent(article.getContent());
        detailDTO.setCommentList(commentDTOList);
        return CommonResult.SUCCESS(detailDTO);
    }


    @Override
    public CommonResult<?> addComment(CommentReqDTO reqDTO) {
        // 判断用户频繁刷评论的控制：一个用户一个小时之内只能刷三条评论，超出的话拒绝
        int commentCount = commentMapper.selectCountOneHour(reqDTO.getEmail());
        if (commentCount > 3) {
            log.error("频繁发表评论, email={}.", reqDTO.getEmail());
            throw new BusinessException(ResultEnum.COMMENT_FREQUENT_ERROR);
        }

        // 插入评论
        Comment comment = new Comment();
        comment.setArticleId(Long.parseLong(reqDTO.getArticleId()));
        comment.setNickname(reqDTO.getNickname());
        comment.setContent(reqDTO.getContent());
        comment.setEmail(reqDTO.getEmail());
        comment.setPersonalSite(reqDTO.getPersonalSite());
        commentMapper.insertSelective(comment);
        return CommonResult.SUCCESS();
    }


}
