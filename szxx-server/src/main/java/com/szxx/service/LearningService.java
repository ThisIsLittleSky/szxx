package com.szxx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.dto.response.LearningRankItemResponse;
import com.szxx.dto.response.LearningRecordResponse;
import com.szxx.dto.response.LearningStatsResponse;

import java.util.List;

public interface LearningService {
    void report(Long materialId, Long userId, int duration, boolean completed);
    IPage<LearningRecordResponse> getRecords(Long userId, int page, int size);
    LearningStatsResponse getStats(Long userId);
    List<LearningRankItemResponse> getRanking(int limit);
}
