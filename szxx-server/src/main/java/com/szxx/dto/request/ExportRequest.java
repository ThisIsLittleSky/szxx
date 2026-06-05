package com.szxx.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ExportRequest {
    @NotEmpty(message = "请选择要导出的素材")
    private List<Long> materialIds;

    private String exportTitle = "思政素材课件";
}
