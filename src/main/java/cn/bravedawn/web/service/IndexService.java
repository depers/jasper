package cn.bravedawn.web.service;

import cn.bravedawn.web.common.CommonResult;

public interface IndexService {

    CommonResult getArticleList(int pageSize, int pageNum);
}
