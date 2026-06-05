package com.szxx.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private long totalMaterials;
    private long totalUsers;
    private long totalViews;
    private long totalDownloads;
    private long pendingReviewCount;
}
