package cn.bravedawn.web.mbg.mapper;

import cn.bravedawn.web.mbg.model.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper {


    List<String> selectTagNameByArticleId(long article);

    int insertBatch(@Param("tagList") List<Tag> tagList);
}