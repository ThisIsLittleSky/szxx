package com.szxx.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParsedDocument {
    private String title;
    private String content;
    private List<String> imageUrls;
}
