package cn.bravedawn.web.service.impl;

import cn.bravedawn.web.common.CommonPageResult;
import cn.bravedawn.web.common.CommonResult;
import cn.bravedawn.web.dto.index.ArticleItemDTO;
import cn.bravedawn.web.mbg.mapper.ArticleMapper;
import cn.bravedawn.web.mbg.mapper.TagMapper;
import cn.bravedawn.web.mbg.model.Article;
import cn.bravedawn.web.service.IndexService;
import cn.bravedawn.web.util.LocalDateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagMapper tagMapper;


    /**
     * 主页文章列表接口
     * @param pageSize 页大小
     * @param pageNum 页数
     * @return 分页之后的数据
     */
    @Override
    public CommonResult getArticleList(int pageSize, int pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articleList = articleMapper.selectArticleList();
        CommonPageResult commonPageResult = CommonPageResult.init(new PageInfo<Article>(articleList));

        List<ArticleItemDTO> articleItems = new ArrayList<>();

        articleList.forEach(article -> {
            ArticleItemDTO item = new ArticleItemDTO();
            item.setArticleId(article.getId().toString());
            item.setTitle(article.getTitle());

            item.setCreateDate(LocalDateUtil.toLocalDateTimeStr(article.getInsertTime()));
            item.setIntro(article.getIntro());
            List<String> tagNames = tagMapper.selectTagNameByArticleId(article.getId());
            item.setTags(tagNames);
            articleItems.add(item);
        });

        commonPageResult.setList(articleItems);

        return CommonResult.SUCCESS(commonPageResult);
    }
}
