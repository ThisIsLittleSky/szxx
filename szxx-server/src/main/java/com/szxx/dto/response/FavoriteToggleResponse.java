package com.szxx.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteToggleResponse {
    private Boolean isFavorited;
    private Integer favoriteCount;
}
