package cn.bravedawn.web.mbg.mapper;

import cn.bravedawn.web.mbg.model.Tag;

import java.util.List;

public interface TagMapper {


    List<String> selectTagNameByArticleId(long article);

}