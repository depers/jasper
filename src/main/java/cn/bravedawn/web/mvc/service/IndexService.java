package cn.bravedawn.web.mvc.service;

import cn.bravedawn.web.mvc.common.CommonResult;
import cn.bravedawn.web.mvc.dto.index.ArticleItem;

import java.util.List;

public interface IndexService {

    CommonResult<List<ArticleItem>> getArticleList(int pageSize, int pageNum);
}
