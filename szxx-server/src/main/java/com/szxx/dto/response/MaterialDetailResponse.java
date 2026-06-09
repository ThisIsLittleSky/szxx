package com.szxx.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.szxx.entity.KnowledgePoint;
import com.szxx.entity.MaterialAttachment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MaterialDetailResponse {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    private String author;
    private String dynasty;
    private String category;
    private String educationLevel;
    private List<String> tags;
    private String coverImage;
    private String summary;
    private String content;
    private String videoUrl;
    private String status;
    private List<KnowledgePoint> knowledgePoints;
    private List<MaterialAttachment> attachments;
    private UploaderInfo uploader;
    private Integer viewCount;
    private Integer favoriteCount;
    private Boolean isFavorited;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class UploaderInfo {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;
        private String nickname;
    }
}
