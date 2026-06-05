package com.szxx.controller.client;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.common.Result;
import com.szxx.dto.request.BatchRemoveRequest;
import com.szxx.dto.response.FavoriteToggleResponse;
import com.szxx.dto.response.MaterialListResponse;
import com.szxx.security.SecurityContextUtil;
import com.szxx.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/toggle/{materialId}")
    public Result<FavoriteToggleResponse> toggle(@PathVariable Long materialId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(favoriteService.toggle(materialId, userId));
    }

    @GetMapping("/check/{materialId}")
    public Result<Map<String, Boolean>> check(@PathVariable Long materialId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(Map.of("isFavorited", favoriteService.isFavorited(materialId, userId)));
    }

    @GetMapping
    public Result<IPage<MaterialListResponse>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String dynasty) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(favoriteService.getFavorites(userId, page, size, category, dynasty));
    }

    @DeleteMapping("/batch")
    public Result<Void> batchRemove(@Valid @RequestBody BatchRemoveRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        favoriteService.batchRemove(request.getMaterialIds(), userId);
        return Result.success();
    }
}
