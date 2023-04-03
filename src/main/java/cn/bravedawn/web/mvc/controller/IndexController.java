package cn.bravedawn.web.mvc.controller;

import cn.bravedawn.web.mvc.annotation.RestController;
import cn.bravedawn.web.mvc.common.CommonResult;
import cn.bravedawn.web.mvc.dto.index.ArticleItem;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

/**
 * @author : fengx9
 * @program : jasper
 * @date : Created in 2022/04/11 3:54 PM
 */

@Path("index")
public class IndexController implements RestController {

    private static final Logger log = LogManager.getLogger(IndexController.class);

    @Path("articleList")
    @GET
    public CommonResult<List<ArticleItem>> getArticleList(int pageSize, int pageNum) {

    }

}
