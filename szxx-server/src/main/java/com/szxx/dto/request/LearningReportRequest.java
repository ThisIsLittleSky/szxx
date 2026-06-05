package com.szxx.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LearningReportRequest {
    @NotNull(message = "素材ID不能为空")
    private Long materialId;

    private Integer duration = 0;
    private Boolean completed = false;
}
