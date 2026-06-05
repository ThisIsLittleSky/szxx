package com.szxx.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadImage(MultipartFile file);
    String uploadAvatar(MultipartFile file, Long userId);
    String uploadAttachment(MultipartFile file, Long materialId);
    void deleteFile(String filePath);
}
