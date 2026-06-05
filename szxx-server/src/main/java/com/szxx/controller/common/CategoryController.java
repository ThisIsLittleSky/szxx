package com.szxx.controller.common;

import com.szxx.common.Result;
import com.szxx.entity.CategoryDict;
import com.szxx.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<Map<String, List<CategoryDict>>> getAll() {
        return Result.success(categoryService.getAllCategories());
    }
}
