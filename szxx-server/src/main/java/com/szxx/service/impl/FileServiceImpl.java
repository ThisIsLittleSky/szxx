package com.szxx.service.impl;

import com.szxx.common.exception.BusinessException;
import com.szxx.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final RestTemplate restTemplate;

    public FileServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
            String urlPath = uploadImage(file);
            // uploadImage returns URL path like "/uploads/images/2026/06/09/uuid.jpg"
            // FileController.download() needs absolute filesystem path
            return uploadPath + urlPath.substring("/uploads/".length());
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

    @Override
    public String downloadAndStoreCoverImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        // Normalize to HTTPS
        if (imageUrl.startsWith("http://")) {
            imageUrl = imageUrl.replaceFirst("^http://", "https://");
        } else if (imageUrl.startsWith("//")) {
            imageUrl = "https:" + imageUrl;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            headers.set("Referer", "https://www.bilibili.com/");
            headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM, MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<byte[]> response =
                    restTemplate.exchange(imageUrl, HttpMethod.GET, entity, byte[].class);
            byte[] imageBytes = response.getBody();
            if (imageBytes == null || imageBytes.length == 0) {
                return null;
            }

            // Determine extension from Content-Type
            MediaType contentType = response.getHeaders().getContentType();
            String ext = "jpg";
            if (contentType != null) {
                String subtype = contentType.getSubtype();
                if (subtype != null && subtype.matches("jpe?g|png|gif|webp")) {
                    ext = subtype.equals("jpeg") ? "jpg" : subtype;
                }
            }

            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String filename = "cover_" + UUID.randomUUID().toString() + "." + ext;
            Path dir = Paths.get(uploadPath, "images", dateDir);
            Files.createDirectories(dir);
            Files.write(dir.resolve(filename), imageBytes);

            return "/uploads/images/" + dateDir + "/" + filename;
        } catch (Exception e) {
            // Download failed, return the original URL as fallback
            return null;
        }
    }

    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }
}
