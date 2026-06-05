package com.szxx.service;

import com.szxx.entity.DownloadLog;
import com.szxx.entity.SearchLog;
import com.szxx.mapper.DownloadLogMapper;
import com.szxx.mapper.SearchLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final SearchLogMapper searchLogMapper;
    private final DownloadLogMapper downloadLogMapper;

    @Async
    public void recordSearchLog(Long userId, String keyword, int resultCount) {
        SearchLog log = new SearchLog();
        log.setUserId(userId);
        log.setKeyword(keyword);
        log.setResultCount(resultCount);
        searchLogMapper.insert(log);
    }

    @Async
    public void recordDownloadLog(Long userId, Long materialId, String downloadType) {
        DownloadLog log = new DownloadLog();
        log.setUserId(userId);
        log.setMaterialId(materialId);
        log.setDownloadType(downloadType);
        downloadLogMapper.insert(log);
    }
}
