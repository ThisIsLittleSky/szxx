package com.szxx.service.impl;

import com.szxx.common.exception.BusinessException;
import com.szxx.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${app.upload.path}")
    private String uploadPath;

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024;
    private static final long MAX_ATTACHMENT_SIZE = 50 * 1024 * 1024;

    @Override
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) throw BusinessException.badRequest("文件为空");
        if (file.getSize() > MAX_IMAGE_SIZE) throw BusinessException.badRequest("图片大小不能超过5MB");

        String ext = getExtension(file);
        if (!ext.matches("jpg|jpeg|png|gif|webp")) throw BusinessException.badRequest("图片格式仅支持jpg/png/gif/webp");

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String filename = UUID.randomUUID().toString() + "." + ext;
        Path dir = Paths.get(uploadPath, "images", dateDir);
        try {
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
        return "/uploads/images/" + dateDir + "/" + filename;
    }

    @Override
    public String uploadAvatar(MultipartFile file, Long userId) {
        if (file.isEmpty()) throw BusinessException.badRequest("文件为空");
        if (file.getSize() > MAX_AVATAR_SIZE) throw BusinessException.badRequest("头像大小不能超过2MB");

        String ext = getExtension(file);
        if (!ext.matches("jpg|jpeg|png")) throw BusinessException.badRequest("头像格式仅支持jpg/png");

        String filename = "user_" + userId + "_" + System.currentTimeMillis() + "." + ext;
        Path dir = Paths.get(uploadPath, "avatars");
        try {
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
        return "/uploads/avatars/" + filename;
    }

    @Override
    public String uploadAttachment(MultipartFile file, Long materialId) {
        if (file.isEmpty()) throw BusinessException.badRequest("文件为空");

        String ext = getExtension(file);

        if (ext.matches("jpg|jpeg|png|gif|webp")) {
            if (file.getSize() > MAX_IMAGE_SIZE) throw BusinessException.badRequest("图片大小不能超过5MB");
            return uploadImage(file);
        }

        if (ext.matches("docx|pdf")) {
            if (file.getSize() > MAX_ATTACHMENT_SIZE) throw BusinessException.badRequest("附件大小不能超过50MB");
        } else {
            throw BusinessException.badRequest("附件仅支持jpg/png/gif/webp/docx/pdf格式");
        }

        String filename = file.getOriginalFilename();
        Path dir = Paths.get(uploadPath, "attachments", "material_" + materialId);
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);
            file.transferTo(target);
            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException ignored) {
        }
    }

    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }
}
