package com.szxx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.dto.response.MaterialListResponse;
import com.szxx.dto.response.SearchResultResponse;

import java.util.List;

public interface SearchService {
    IPage<SearchResultResponse> search(String keyword, int page, int size);
    List<String> suggest(String keyword);
    List<HotSearchItem> hotSearch();
    IPage<MaterialListResponse> getFilteredMaterials(String dynasty, String category,
                                                     String educationLevel, String keyword, int page, int size);

    record HotSearchItem(String keyword, int count) {}
}
