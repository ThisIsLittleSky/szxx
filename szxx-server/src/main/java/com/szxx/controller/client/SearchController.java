package com.szxx.controller.client;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.common.Result;
import com.szxx.dto.response.MaterialListResponse;
import com.szxx.dto.response.SearchResultResponse;
import com.szxx.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public Result<IPage<SearchResultResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return Result.success(searchService.search(keyword, page, size));
    }

    @GetMapping("/search/suggest")
    public Result<List<String>> suggest(@RequestParam String keyword) {
        return Result.success(searchService.suggest(keyword));
    }

    @GetMapping("/search/hot")
    public Result<List<SearchService.HotSearchItem>> hotSearch() {
        return Result.success(searchService.hotSearch());
    }

    @GetMapping("/materials/filter")
    public Result<IPage<MaterialListResponse>> filter(
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String educationLevel,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return Result.success(searchService.getFilteredMaterials(
                dynasty, category, educationLevel, keyword, page, size));
    }
}
