package cn.bravedawn.web.mvc.service.impl;

import cn.bravedawn.web.mvc.common.CommonResult;
import cn.bravedawn.web.mvc.dto.index.ArticleItem;
import cn.bravedawn.web.mvc.service.IndexService;

import java.util.List;

public class IndexServiceImpl implements IndexService {


    @Override
    public CommonResult<List<ArticleItem>> getArticleList(int pageSize, int pageNum) {
        return null;
    }
}
