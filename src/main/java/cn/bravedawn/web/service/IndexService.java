package cn.bravedawn.web.service;

import cn.bravedawn.web.common.CommonResult;
import cn.bravedawn.web.dto.index.ArticleItem;

import java.util.List;

public interface IndexService {

    CommonResult<List<ArticleItem>> getArticleList(int pageSize, int pageNum);
}
