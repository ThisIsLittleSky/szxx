package com.szxx.controller.client;

import com.szxx.dto.request.ExportRequest;
import com.szxx.security.SecurityContextUtil;
import com.szxx.service.ExportService;
import com.szxx.service.LogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;
    private final LogService logService;

    @PostMapping("/word")
    @PreAuthorize("hasAnyRole('teacher', 'admin')")
    public ResponseEntity<byte[]> exportWord(@Valid @RequestBody ExportRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        byte[] data = exportService.exportWord(request.getMaterialIds(), request.getExportTitle());

        for (Long materialId : request.getMaterialIds()) {
            logService.recordDownloadLog(userId, materialId, "export");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=export.docx")
                .body(data);
    }

    @PostMapping("/ppt")
    public ResponseEntity<byte[]> exportPpt(@RequestParam Long materialId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        byte[] data = exportService.exportPpt(materialId);

        logService.recordDownloadLog(userId, materialId, "ppt");

        String filename = exportService.buildPptFilename(materialId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + filename)
                .body(data);
    }
}
