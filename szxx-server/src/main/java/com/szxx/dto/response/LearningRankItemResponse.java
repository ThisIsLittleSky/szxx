package com.szxx.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningRankItemResponse {
    private int rank;
    private Long userId;
    private String nickname;
    private String username;
    private String avatar;
    private int totalDuration;
}
