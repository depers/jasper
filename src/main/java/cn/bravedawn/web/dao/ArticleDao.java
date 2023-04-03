package cn.bravedawn.web.dao;

import cn.bravedawn.web.entity.Article;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ArticleDao {


    @Select("SELECT title, intro, content, path, author, publish_date FROM article")
    @Results({
            @Result(column = "publish_date", property = "publishDate")
    })
    List<Article> selectList(RowBounds rowBounds);
}
