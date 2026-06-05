package com.szxx.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.common.Result;
import com.szxx.dto.request.ReviewRequest;
import com.szxx.entity.Material;
import com.szxx.mapper.MaterialMapper;
import com.szxx.security.SecurityContextUtil;
import com.szxx.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/materials")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class AdminMaterialController {

    private final MaterialMapper materialMapper;
    private final MaterialService materialService;

    @GetMapping
    public Result<IPage<Material>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long uploaderId) {

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Material::getStatus, status);
        if (category != null) wrapper.eq(Material::getCategory, category);
        if (uploaderId != null) wrapper.eq(Material::getUploaderId, uploaderId);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Material::getTitle, keyword).or().like(Material::getContent, keyword));
        }
        wrapper.orderByDesc(Material::getCreatedAt);

        return Result.success(materialMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @PutMapping("/{id}/review")
    public Result<Void> review(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            return Result.error(404, "素材不存在");
        }
        material.setStatus(request.getStatus());
        material.setReviewComment(request.getReviewComment());
        materialMapper.updateById(material);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        materialService.deleteMaterial(id, userId);
        return Result.success();
    }
}
