package com.szxx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.entity.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    @Select("<script>" +
            "SELECT * FROM material WHERE 1=1" +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>" +
            "<if test='uploaderId != null'> AND uploader_id = #{uploaderId}</if>" +
            "<if test='dynasty != null and dynasty != \"\"'> AND dynasty = #{dynasty}</if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category}</if>" +
            "<if test='educationLevel != null and educationLevel != \"\"'> AND education_level = #{educationLevel}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR content LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "<choose>" +
            "  <when test='sort == \"hot\"'> ORDER BY (view_count + favorite_count * 10) DESC</when>" +
            "  <when test='sort == \"downloads\"'> ORDER BY download_count DESC</when>" +
            "  <otherwise> ORDER BY created_at DESC</otherwise>" +
            "</choose>" +
            "</script>")
    IPage<Material> selectPageWithFilters(Page<Material> page,
                                          @Param("status") String status,
                                          @Param("uploaderId") Long uploaderId,
                                          @Param("dynasty") String dynasty,
                                          @Param("category") String category,
                                          @Param("educationLevel") String educationLevel,
                                          @Param("keyword") String keyword,
                                          @Param("sort") String sort);
}
