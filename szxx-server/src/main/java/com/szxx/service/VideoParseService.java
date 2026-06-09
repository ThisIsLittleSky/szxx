package com.szxx.service;

import com.szxx.dto.response.VideoParseResponse;

public interface VideoParseService {
    VideoParseResponse parseVideoUrl(String url);
}
