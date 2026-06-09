package com.szxx.service;

import java.util.List;

public interface ExportService {
    byte[] exportWord(List<Long> materialIds, String exportTitle);
    byte[] exportPpt(Long materialId);
    String buildPptFilename(Long materialId);
}
