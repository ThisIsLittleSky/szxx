package com.szxx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.dto.request.MaterialQuery;
import com.szxx.dto.response.BatchUploadResponse;
import com.szxx.dto.response.MaterialDetailResponse;
import com.szxx.dto.response.MaterialListResponse;
import com.szxx.entity.Material;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MaterialService {
    Material createMaterial(String title, String author, String dynasty, String category,
                            String educationLevel, String tags, String content, String videoUrl,
                            MultipartFile coverImage, List<MultipartFile> attachments, Long uploaderId);

    Material updateMaterial(Long id, String title, String author, String dynasty, String category,
                            String educationLevel, String tags, String content, String videoUrl,
                            MultipartFile coverImage, List<MultipartFile> attachments, Long userId);

    void deleteMaterial(Long id, Long userId);

    MaterialDetailResponse getMaterialDetail(Long id, Long currentUserId);

    IPage<MaterialListResponse> getMaterialPage(MaterialQuery query);

    BatchUploadResponse batchUpload(List<MultipartFile> files, String dynasty,
                                    String category, String educationLevel, Long uploaderId);
}
