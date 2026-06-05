package com.szxx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.dto.response.LearningStatsResponse;
import com.szxx.entity.LearningRecord;

public interface LearningService {
    void report(Long materialId, Long userId, int duration, boolean completed);
    IPage<LearningRecord> getRecords(Long userId, int page, int size);
    LearningStatsResponse getStats(Long userId);
}
