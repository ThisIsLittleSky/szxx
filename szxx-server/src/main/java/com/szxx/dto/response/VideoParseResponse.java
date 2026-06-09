package com.szxx.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoParseResponse {
    private String title;
    private String description;
    private String coverUrl;
}
