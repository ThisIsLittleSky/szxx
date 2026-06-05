package com.szxx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szxx.entity.CategoryDict;
import com.szxx.mapper.CategoryDictMapper;
import com.szxx.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDictMapper categoryDictMapper;

    @Override
    public Map<String, List<CategoryDict>> getAllCategories() {
        List<CategoryDict> all = categoryDictMapper.selectList(
                new LambdaQueryWrapper<CategoryDict>().orderByAsc(CategoryDict::getSortOrder));

        Map<String, List<CategoryDict>> result = new LinkedHashMap<>();
        result.put("dynasties", all.stream().filter(c -> "dynasty".equals(c.getType())).collect(Collectors.toList()));
        result.put("categories", all.stream().filter(c -> "category".equals(c.getType())).collect(Collectors.toList()));
        result.put("education_levels", all.stream().filter(c -> "education_level".equals(c.getType())).collect(Collectors.toList()));
        return result;
    }
}
