package com.szxx.service;

import com.szxx.entity.CategoryDict;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    Map<String, List<CategoryDict>> getAllCategories();
}
