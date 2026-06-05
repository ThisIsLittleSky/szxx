package com.szxx.dto.request;

import lombok.Data;

@Data
public class MaterialQuery {
    private Integer page = 1;
    private Integer size = 12;
    private String keyword;
    private String dynasty;
    private String category;
    private String educationLevel;
    private String sort;
}
