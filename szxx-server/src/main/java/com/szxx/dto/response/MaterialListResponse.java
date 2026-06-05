package com.szxx.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MaterialListResponse {
    private Long id;
    private String title;
    private String author;
    private String dynasty;
    private String dynastyCode;
    private String category;
    private String categoryCode;
    private String educationLevel;
    private String educationCode;
    private List<String> tags;
    private String coverImage;
    private String summary;
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
}
