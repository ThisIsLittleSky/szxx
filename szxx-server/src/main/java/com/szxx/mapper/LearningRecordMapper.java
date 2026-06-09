package com.szxx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.dto.response.LearningRankItemResponse;
import com.szxx.dto.response.LearningRecordResponse;
import com.szxx.entity.LearningRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LearningRecordMapper extends BaseMapper<LearningRecord> {

    @Select("SELECT lr.id, lr.material_id AS materialId, m.title AS materialTitle, " +
            "m.author AS materialAuthor, lr.duration, lr.completed, lr.updated_at AS updatedAt " +
            "FROM learning_record lr " +
            "JOIN material m ON lr.material_id = m.id " +
            "WHERE lr.user_id = #{userId} AND m.status = 'approved' " +
            "ORDER BY lr.updated_at DESC")
    IPage<LearningRecordResponse> selectRecordsWithMaterial(Page<?> page, @Param("userId") Long userId);

    @Select("SELECT u.id AS userId, u.nickname, u.username, u.avatar, " +
            "COALESCE(SUM(lr.duration), 0) AS totalDuration " +
            "FROM learning_record lr " +
            "JOIN user u ON lr.user_id = u.id " +
            "WHERE u.status = 'active' " +
            "GROUP BY lr.user_id " +
            "ORDER BY totalDuration DESC " +
            "LIMIT #{limit}")
    List<LearningRankItemResponse> selectRanking(int limit);
}
