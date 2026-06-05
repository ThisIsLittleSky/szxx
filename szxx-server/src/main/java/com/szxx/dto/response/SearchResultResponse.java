package com.szxx.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SearchResultResponse {
    private Long id;
    private String title;
    private String highlightTitle;
    private String author;
    private String dynasty;
    private String category;
    private String educationLevel;
    private String highlightContent;
    private String coverImage;
    private Integer viewCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
}
