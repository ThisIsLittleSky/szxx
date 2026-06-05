package com.szxx.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szxx.common.Result;
import com.szxx.dto.response.DashboardResponse;
import com.szxx.entity.Material;
import com.szxx.entity.User;
import com.szxx.mapper.MaterialMapper;
import com.szxx.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class AdminDashboardController {

    private final MaterialMapper materialMapper;
    private final UserMapper userMapper;

    @GetMapping("/dashboard")
    public Result<DashboardResponse> dashboard() {
        long totalMaterials = materialMapper.selectCount(null);
        long totalUsers = userMapper.selectCount(null);
        long pendingReviewCount = materialMapper.selectCount(
                new LambdaQueryWrapper<Material>().eq(Material::getStatus, "pending"));

        List<Material> allMaterials = materialMapper.selectList(null);
        long totalViews = allMaterials.stream().mapToLong(m -> m.getViewCount() != null ? m.getViewCount() : 0).sum();
        long totalDownloads = allMaterials.stream().mapToLong(m -> m.getDownloadCount() != null ? m.getDownloadCount() : 0).sum();

        return Result.success(DashboardResponse.builder()
                .totalMaterials(totalMaterials)
                .totalUsers(totalUsers)
                .totalViews(totalViews)
                .totalDownloads(totalDownloads)
                .pendingReviewCount(pendingReviewCount)
                .build());
    }

    @GetMapping("/stats/materials-by-category")
    public Result<List<Map<String, Object>>> materialsByCategory() {
        List<Material> materials = materialMapper.selectList(null);
        Map<String, Long> categoryCount = new LinkedHashMap<>();
        for (Material m : materials) {
            categoryCount.merge(m.getCategory(), 1L, Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        categoryCount.forEach((cat, count) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("category", cat);
            item.put("count", count);
            result.add(item);
        });
        return Result.success(result);
    }

    @GetMapping("/stats/top-materials")
    public Result<List<Map<String, Object>>> topMaterials(
            @RequestParam(defaultValue = "views") String type,
            @RequestParam(defaultValue = "10") int limit) {

        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if ("downloads".equals(type)) {
            wrapper.orderByDesc(Material::getDownloadCount);
        } else {
            wrapper.orderByDesc(Material::getViewCount);
        }
        wrapper.last("LIMIT " + limit);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Material m : materialMapper.selectList(wrapper)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.getId());
            item.put("title", m.getTitle());
            item.put("view_count", m.getViewCount());
            item.put("download_count", m.getDownloadCount());
            result.add(item);
        }
        return Result.success(result);
    }

    @GetMapping("/stats/user-activity")
    public Result<List<Map<String, Object>>> userActivity(
            @RequestParam(defaultValue = "30") int days) {

        // Generate date series (simplified: return empty structure for now)
        List<Map<String, Object>> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = days - 1; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", String.format("%tF", cal));
            item.put("active_users", 0);
            item.put("new_users", 0);
            result.add(item);
        }
        return Result.success(result);
    }
}
