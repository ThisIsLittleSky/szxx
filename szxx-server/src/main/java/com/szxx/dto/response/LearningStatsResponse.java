package com.szxx.dto.response;

import com.szxx.entity.LearningRecord;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LearningStatsResponse {
    private int totalMaterials;
    private int totalDuration;
    private int completedCount;
    private List<LearningRecord> recentRecords;
}
