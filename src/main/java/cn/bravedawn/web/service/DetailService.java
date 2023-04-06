package cn.bravedawn.web.service;

import cn.bravedawn.web.common.CommonResult;
import cn.bravedawn.web.dto.detail.ArticleDetailDTO;
import cn.bravedawn.web.dto.detail.CommentReqDTO;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/5 14:23
 */
public interface DetailService {

    CommonResult<ArticleDetailDTO> getDetail(long articleId);

    CommonResult<?> addComment(CommentReqDTO reqDTO);
}
