package com.szxx.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BatchUploadResponse {
    private int total;
    private int success;
    private int failed;
    private List<BatchUploadItem> results;

    @Data
    @Builder
    public static class BatchUploadItem {
        private String filename;
        @JsonSerialize(using = ToStringSerializer.class)
        private Long materialId;
        private String status;
        private String error;
    }
}
