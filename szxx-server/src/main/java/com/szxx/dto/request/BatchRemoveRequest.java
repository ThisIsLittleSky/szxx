package com.szxx.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BatchRemoveRequest {
    @NotEmpty(message = "请选择要删除的项目")
    private List<Long> materialIds;
}
