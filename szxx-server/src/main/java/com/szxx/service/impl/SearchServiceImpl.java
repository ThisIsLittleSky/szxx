package com.szxx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.dto.request.MaterialQuery;
import com.szxx.dto.response.MaterialListResponse;
import com.szxx.dto.response.SearchResultResponse;
import com.szxx.entity.Material;
import com.szxx.mapper.MaterialMapper;
import com.szxx.mapper.SearchLogMapper;
import com.szxx.mapper.TagMapper;
import com.szxx.service.LogService;
import com.szxx.service.MaterialService;
import com.szxx.service.SearchService;
import com.szxx.util.JiebaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final MaterialMapper materialMapper;
    private final TagMapper tagMapper;
    private final SearchLogMapper searchLogMapper;
    private final MaterialService materialService;
    private final JiebaUtil jiebaUtil;
    private final LogService logService;

    @Override
    public IPage<SearchResultResponse> search(String keyword, int queryPage, int size) {
        List<String> tokens = jiebaUtil.segment(keyword);

        // Build query: use LIKE for each segmented token
        Page<Material> page = new Page<>(queryPage, size);
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<Material>()
                .eq(Material::getStatus, "approved");

        if (!tokens.isEmpty()) {
            wrapper.and(w -> {
                w.like(Material::getTitle, keyword)
                    .or().like(Material::getContent, keyword)
                    .or().like(Material::getTags, keyword);
                for (String token : tokens) {
                    w.or().like(Material::getTitle, token)
                        .or().like(Material::getContent, token)
                        .or().like(Material::getTags, token);
                }
            });
        } else {
            wrapper.and(w -> w.like(Material::getTitle, keyword)
                    .or().like(Material::getContent, keyword)
                    .or().like(Material::getTags, keyword));
        }
        wrapper.orderByDesc(Material::getCreatedAt);

        IPage<Material> result = materialMapper.selectPage(page, wrapper);

        // record search log asynchronously
        logService.recordSearchLog(null, keyword, (int) result.getTotal());

        return result.convert(m -> toSearchResponse(m, tokens, keyword));
    }

    @Override
    public List<String> suggest(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();

        // From tag table
        var tags = tagMapper.selectList(
                new LambdaQueryWrapper<com.szxx.entity.Tag>()
                        .likeRight(com.szxx.entity.Tag::getName, keyword)
                        .last("LIMIT 5"));

        Set<String> result = new LinkedHashSet<>();
        for (var t : tags) {
            result.add(t.getName());
        }

        // From search log
        var logs = searchLogMapper.selectList(
                new LambdaQueryWrapper<com.szxx.entity.SearchLog>()
                        .select(com.szxx.entity.SearchLog::getKeyword)
                        .likeRight(com.szxx.entity.SearchLog::getKeyword, keyword)
                        .groupBy(com.szxx.entity.SearchLog::getKeyword)
                        .last("ORDER BY COUNT(*) DESC LIMIT 5"));

        for (var l : logs) {
            result.add(l.getKeyword());
        }

        return result.stream().limit(10).toList();
    }

    @Override
    public List<HotSearchItem> hotSearch() {
        // Using MP's query with custom SQL via mapper
        var logs = searchLogMapper.selectList(
                new LambdaQueryWrapper<com.szxx.entity.SearchLog>()
                        .select(com.szxx.entity.SearchLog::getKeyword)
                        .apply("created_at > DATE_SUB(NOW(), INTERVAL 7 DAY)")
                        .groupBy(com.szxx.entity.SearchLog::getKeyword)
                        .last("ORDER BY COUNT(*) DESC LIMIT 10"));

        Map<String, Integer> countMap = new LinkedHashMap<>();
        for (var l : logs) {
            countMap.merge(l.getKeyword(), 1, Integer::sum);
        }

        return countMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(e -> new HotSearchItem(e.getKey(), e.getValue()))
                .toList();
    }

    @Override
    public IPage<MaterialListResponse> getFilteredMaterials(String dynasty, String category,
                                                            String educationLevel, String keyword, int queryPage, int size) {
        MaterialQuery materialQuery = new MaterialQuery();
        materialQuery.setDynasty(dynasty);
        materialQuery.setCategory(category);
        materialQuery.setEducationLevel(educationLevel);
        materialQuery.setKeyword(keyword);
        materialQuery.setPage(queryPage);
        materialQuery.setSize(size);
        return materialService.getMaterialPage(materialQuery);
    }

    private SearchResultResponse toSearchResponse(Material m, List<String> tokens, String originalKeyword) {
        Set<String> seen = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        List<String> allTerms = new ArrayList<>();
        if (originalKeyword != null && !originalKeyword.isEmpty()) {
            allTerms.add(originalKeyword);
            seen.add(originalKeyword);
        }
        for (String t : tokens) {
            if (t != null && !t.isEmpty() && seen.add(t)) {
                allTerms.add(t);
            }
        }

        String highlightTitle = highlight(m.getTitle(), allTerms);
        String highlightContent = highlight(
                m.getContent() != null ? m.getContent().replaceAll("<[^>]+>", "") : "", allTerms);

        if (highlightContent.length() > 300) {
            highlightContent = highlightContent.substring(0, 300) + "...";
        }

        return SearchResultResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .highlightTitle(highlightTitle)
                .author(m.getAuthor())
                .dynasty(m.getDynasty())
                .category(m.getCategory())
                .educationLevel(m.getEducationLevel())
                .highlightContent(highlightContent)
                .coverImage(m.getCoverImage())
                .viewCount(m.getViewCount())
                .favoriteCount(m.getFavoriteCount())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private String highlight(String text, List<String> terms) {
        if (text == null) return "";
        String result = text;
        for (String term : terms) {
            if (term == null || term.isEmpty()) continue;
            result = result.replaceAll("(?i)" + Pattern.quote(term), "<em>" + term + "</em>");
        }
        return result;
    }
}
