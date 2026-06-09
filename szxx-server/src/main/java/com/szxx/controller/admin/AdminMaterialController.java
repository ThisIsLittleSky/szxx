package com.szxx.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.common.Result;
import com.szxx.dto.request.ReviewRequest;
import com.szxx.entity.Material;
import com.szxx.entity.MaterialAttachment;
import com.szxx.entity.User;
import com.szxx.mapper.MaterialAttachmentMapper;
import com.szxx.mapper.MaterialMapper;
import com.szxx.mapper.UserMapper;
import com.szxx.security.SecurityContextUtil;
import com.szxx.service.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/materials")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class AdminMaterialController {

    private final MaterialMapper materialMapper;
    private final MaterialService materialService;
    private final UserMapper userMapper;
    private final MaterialAttachmentMapper materialAttachmentMapper;

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

        IPage<Material> materialPage = materialMapper.selectPage(new Page<>(page, size), wrapper);

        // 批量填充上传人名称
        List<Long> uploaderIds = materialPage.getRecords().stream()
                .map(Material::getUploaderId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (!uploaderIds.isEmpty()) {
            Map<Long, String> nameMap = userMapper.selectBatchIds(uploaderIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u.getNickname() != null ? u.getNickname() : u.getUsername()));
            materialPage.getRecords().forEach(m -> m.setUploaderName(nameMap.get(m.getUploaderId())));
        }

        return Result.success(materialPage);
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Material material = materialMapper.selectById(id);
        if (material == null) {
            return Result.error(404, "素材不存在");
        }

        // 填充上传人名称
        User uploader = userMapper.selectById(material.getUploaderId());
        if (uploader != null) {
            material.setUploaderName(uploader.getNickname() != null ? uploader.getNickname() : uploader.getUsername());
        }

        // 查询附件列表
        List<MaterialAttachment> attachments = materialAttachmentMapper.selectList(
                new LambdaQueryWrapper<MaterialAttachment>().eq(MaterialAttachment::getMaterialId, id));

        Map<String, Object> result = new HashMap<>();
        result.put("material", material);
        result.put("attachments", attachments);
        return Result.success(result);
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
