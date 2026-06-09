package com.szxx.controller.common;

import com.szxx.common.Result;
import com.szxx.common.exception.BusinessException;
import com.szxx.entity.MaterialAttachment;
import com.szxx.mapper.MaterialAttachmentMapper;
import com.szxx.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final MaterialAttachmentMapper materialAttachmentMapper;

    @Value("${app.upload.path}")
    private String uploadPath;

    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file);
        return Result.success(Map.of("url", url));
    }

    @PostMapping("/upload/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = 1L; // will be replaced by current user
        String url = fileService.uploadAvatar(file, userId);
        return Result.success(Map.of("url", url));
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> download(@PathVariable Long attachmentId,
                                             @RequestParam(required = false, defaultValue = "true") boolean download) {
        MaterialAttachment attachment = materialAttachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw BusinessException.notFound("附件不存在");
        }

        String filePath = attachment.getFilePath();
        // Safety net: if filePath was stored as a URL path (e.g. "/uploads/images/..."),
        // resolve it to the absolute filesystem path
        if (filePath.startsWith("/uploads/")) {
            filePath = uploadPath + filePath.substring("/uploads/".length());
        }

        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            throw BusinessException.notFound("附件文件不存在");
        }

        String encodedFilename = URLEncoder.encode(attachment.getFilename(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        MediaType mediaType = MediaTypeFactory.getMediaType(attachment.getFilename())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        String disposition = download ? "attachment" : "inline";

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }
}
