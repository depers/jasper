package cn.bravedawn.web.controller;

import cn.bravedawn.web.common.CommonResult;
import cn.bravedawn.web.dto.index.ArticleItem;
import cn.bravedawn.web.service.IndexService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : fengx9
 * @program : jasper
 * @date : Created in 2022/04/11 3:54 PM
 */

@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    private static final Logger log = LogManager.getLogger(IndexController.class);

    /**
     * 首页-分页查询文章信息
     * @param pageSize 页大小
     * @param pageNum 页数
     * @return 文章简要信息列表
     */
    @GetMapping("/article/list")
    public CommonResult<List<ArticleItem>> getArticleList(@RequestParam(required = false, defaultValue = "10") int pageSize,
                                                          @RequestParam(required = false, defaultValue = "1") int pageNum) {
        return indexService.getArticleList(pageSize, pageNum);
    }

}
