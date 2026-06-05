package com.szxx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.dto.response.FavoriteToggleResponse;
import com.szxx.dto.response.MaterialListResponse;

import java.util.List;

public interface FavoriteService {
    FavoriteToggleResponse toggle(Long materialId, Long userId);
    boolean isFavorited(Long materialId, Long userId);
    IPage<MaterialListResponse> getFavorites(Long userId, int page, int size, String category, String dynasty);
    void batchRemove(List<Long> materialIds, Long userId);
}
