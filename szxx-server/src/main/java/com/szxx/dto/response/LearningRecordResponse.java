package com.szxx.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LearningRecordResponse {
    private Long id;
    private Long materialId;
    private String materialTitle;
    private String materialAuthor;
    private Integer duration;
    private Integer completed;
    private LocalDateTime updatedAt;
}
