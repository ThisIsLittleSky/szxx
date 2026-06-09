package com.szxx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.common.exception.BusinessException;
import com.szxx.dto.response.LearningRankItemResponse;
import com.szxx.dto.response.LearningRecordResponse;
import com.szxx.dto.response.LearningStatsResponse;
import com.szxx.entity.LearningRecord;
import com.szxx.mapper.LearningRecordMapper;
import com.szxx.mapper.MaterialMapper;
import com.szxx.service.LearningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LearningServiceImpl implements LearningService {

    private final LearningRecordMapper learningRecordMapper;
    private final MaterialMapper materialMapper;

    @Override
    public void report(Long materialId, Long userId, int duration, boolean completed) {
        if (materialMapper.selectById(materialId) == null) {
            throw BusinessException.notFound("素材不存在");
        }

        LearningRecord existing = learningRecordMapper.selectOne(new LambdaQueryWrapper<LearningRecord>()
                .eq(LearningRecord::getUserId, userId)
                .eq(LearningRecord::getMaterialId, materialId));

        if (existing != null) {
            existing.setDuration(existing.getDuration() + duration);
            existing.setCompleted(completed ? 1 : 0);
            learningRecordMapper.updateById(existing);
        } else {
            LearningRecord record = new LearningRecord();
            record.setUserId(userId);
            record.setMaterialId(materialId);
            record.setDuration(duration);
            record.setCompleted(completed ? 1 : 0);
            learningRecordMapper.insert(record);
        }
    }

    @Override
    public IPage<LearningRecordResponse> getRecords(Long userId, int page, int size) {
        return learningRecordMapper.selectRecordsWithMaterial(new Page<>(page, size), userId);
    }

    @Override
    public LearningStatsResponse getStats(Long userId) {
        List<LearningRecord> records = learningRecordMapper.selectList(
                new LambdaQueryWrapper<LearningRecord>().eq(LearningRecord::getUserId, userId));

        int totalMaterials = records.size();
        int totalDuration = records.stream().mapToInt(LearningRecord::getDuration).sum();
        int completedCount = (int) records.stream().filter(r -> r.getCompleted() == 1).count();

        List<LearningRecord> recentRecords = learningRecordMapper.selectList(
                new LambdaQueryWrapper<LearningRecord>()
                        .eq(LearningRecord::getUserId, userId)
                        .orderByDesc(LearningRecord::getUpdatedAt)
                        .last("LIMIT 5"));

        return LearningStatsResponse.builder()
                .totalMaterials(totalMaterials)
                .totalDuration(totalDuration)
                .completedCount(completedCount)
                .recentRecords(recentRecords)
                .build();
    }

    @Override
    public List<LearningRankItemResponse> getRanking(int limit) {
        List<LearningRankItemResponse> list = learningRecordMapper.selectRanking(limit);
        AtomicInteger rank = new AtomicInteger(1);
        list.forEach(item -> item.setRank(rank.getAndIncrement()));
        return list;
    }
}
