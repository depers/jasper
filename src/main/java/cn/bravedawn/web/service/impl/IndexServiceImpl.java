package cn.bravedawn.web.service.impl;

import cn.bravedawn.web.common.CommonResult;
import cn.bravedawn.web.dto.index.ArticleItem;
import cn.bravedawn.web.mbg.mapper.ArticleMapper;
import cn.bravedawn.web.mbg.model.Article;
import cn.bravedawn.web.service.IndexService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ArticleMapper articleMapper;


    @Override
    public CommonResult<List<ArticleItem>> getArticleList(int pageSize, int pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articleItems = articleMapper.selectArticleList();


        articleItems.forEach(article -> {

        });

        return null;
    }
}
