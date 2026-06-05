package com.szxx.controller.client;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.common.Result;
import com.szxx.dto.request.MaterialQuery;
import com.szxx.dto.response.BatchUploadResponse;
import com.szxx.dto.response.MaterialDetailResponse;
import com.szxx.dto.response.MaterialListResponse;
import com.szxx.entity.Material;
import com.szxx.security.SecurityContextUtil;
import com.szxx.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public Result<IPage<MaterialListResponse>> list(MaterialQuery query) {
        return Result.success(materialService.getMaterialPage(query));
    }

    @GetMapping("/{id}")
    public Result<MaterialDetailResponse> getDetail(@PathVariable Long id) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(materialService.getMaterialDetail(id, userId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('teacher', 'admin')")
    public Result<Material> create(
            @RequestParam("title") String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam("dynasty") String dynasty,
            @RequestParam("category") String category,
            @RequestParam("educationLevel") String educationLevel,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "videoUrl", required = false) String videoUrl,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments) {

        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(materialService.createMaterial(
                title, author, dynasty, category, educationLevel,
                tags, content, videoUrl, coverImage, attachments, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('teacher', 'admin')")
    public Result<Material> update(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "dynasty", required = false) String dynasty,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "educationLevel", required = false) String educationLevel,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "videoUrl", required = false) String videoUrl,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments) {

        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(materialService.updateMaterial(
                id, title, author, dynasty, category, educationLevel,
                tags, content, videoUrl, coverImage, attachments, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('teacher', 'admin')")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        materialService.deleteMaterial(id, userId);
        return Result.success();
    }

    @PostMapping("/batch-upload")
    @PreAuthorize("hasAnyRole('teacher', 'admin')")
    public Result<BatchUploadResponse> batchUpload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("dynasty") String dynasty,
            @RequestParam("category") String category,
            @RequestParam("educationLevel") String educationLevel) {

        if (files.size() > 10) {
            return Result.error(400, "单次最多上传10个文件");
        }

        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(materialService.batchUpload(files, dynasty, category, educationLevel, userId));
    }
}
