package cn.bravedawn.web.controller;


import cn.bravedawn.web.common.CommonResult;
import cn.bravedawn.web.common.ResultEnum;
import cn.bravedawn.web.dto.detail.ArticleDetailDTO;
import cn.bravedawn.web.dto.detail.CommentReqDTO;
import cn.bravedawn.web.exception.BusinessException;
import cn.bravedawn.web.service.DetailService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/detail")
public class DetailController {

    private static final Logger log = LoggerFactory.getLogger(DetailController.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(.+)$");

    @Autowired
    private DetailService detailService;


    /**
     * 获取文章详情页面（包括评论）
     * @param id 文章id
     * @return 文章详细信息和评论
     */
    @RequestMapping("/{id}")
    public CommonResult<ArticleDetailDTO> getDetail(@PathVariable(required = false, name = "id") String id) {

        if (StringUtils.isBlank(id)) {
            log.error("请求参数id为空");
            throw new BusinessException(ResultEnum.REQUEST_PARAMS_ERROR);
        }

        return detailService.getDetail(Long.parseLong(id));
    }


    /**
     * 添加评论
     * @param commentReqDTO 请求参数
     * @return 评论结果
     */
    @PostMapping("/comment")
    public CommonResult<?> comment(@RequestBody(required = false) CommentReqDTO commentReqDTO) {

        if (StringUtils.isBlank(commentReqDTO.getArticleId())) {
            log.error("请求参数文章id为空");
            throw new BusinessException(ResultEnum.REQUEST_PARAMS_ERROR);
        }

        if (StringUtils.isBlank(commentReqDTO.getNickname())) {
            log.error("请求参数昵称为空");
            throw new BusinessException(ResultEnum.COMMENT_NICKNAME_ERROR);
        }

        if (StringUtils.isBlank(commentReqDTO.getContent())) {
            log.error("请求参数评论内容为空");
            throw new BusinessException(ResultEnum.COMMENT_CONTENT_ERROR);
        }

        if (StringUtils.isBlank(commentReqDTO.getEmail())) {
            log.error("请求参数电子邮箱为空");
            throw new BusinessException(ResultEnum.COMMENT_EMAIL_ERROR);
        }

        if (!EMAIL_PATTERN.matcher(commentReqDTO.getEmail()).matches()) {
            log.error("邮箱格式错误, email={}.", commentReqDTO.getEmail());
            throw new BusinessException(ResultEnum.COMMENT_EMAIL_FORMAT_ERROR);
        }

        return detailService.addComment(commentReqDTO);
    }
}
