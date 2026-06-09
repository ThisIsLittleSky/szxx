package com.szxx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szxx.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @Select("SELECT * FROM tag WHERE name LIKE CONCAT('%',#{keyword},'%') ORDER BY name LIMIT 10")
    List<Tag> searchByName(@Param("keyword") String keyword);

    @Select("SELECT t.id, t.name, t.created_at, COUNT(mt.material_id) AS material_count FROM tag t LEFT JOIN material_tag mt ON t.id = mt.tag_id GROUP BY t.id, t.name, t.created_at ORDER BY t.name")
    List<Tag> listTagsWithCount();
}
