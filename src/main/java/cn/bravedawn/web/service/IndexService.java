package cn.bravedawn.web.service;

import cn.bravedawn.web.common.CommonPageResult;
import cn.bravedawn.web.common.CommonResult;

public interface IndexService {

    CommonResult<CommonPageResult> getArticleList(int pageSize, int pageNum);
}
