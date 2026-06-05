package com.szxx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.common.exception.BusinessException;
import com.szxx.dto.response.FavoriteToggleResponse;
import com.szxx.dto.response.MaterialListResponse;
import com.szxx.entity.Favorite;
import com.szxx.entity.Material;
import com.szxx.mapper.FavoriteMapper;
import com.szxx.mapper.MaterialMapper;
import com.szxx.service.FavoriteService;
import com.szxx.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final MaterialMapper materialMapper;
    private final MaterialService materialService;

    @Override
    @Transactional
    public FavoriteToggleResponse toggle(Long materialId, Long userId) {
        Material material = materialMapper.selectById(materialId);
        if (material == null || !"approved".equals(material.getStatus())) {
            throw BusinessException.notFound("素材不存在");
        }

        Favorite existing = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getMaterialId, materialId));

        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            material.setFavoriteCount(Math.max(0, material.getFavoriteCount() - 1));
            materialMapper.updateById(material);
            return FavoriteToggleResponse.builder().isFavorited(false).favoriteCount(material.getFavoriteCount()).build();
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setMaterialId(materialId);
            favoriteMapper.insert(favorite);
            material.setFavoriteCount(material.getFavoriteCount() + 1);
            materialMapper.updateById(material);
            return FavoriteToggleResponse.builder().isFavorited(true).favoriteCount(material.getFavoriteCount()).build();
        }
    }

    @Override
    public boolean isFavorited(Long materialId, Long userId) {
        return favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getMaterialId, materialId)) > 0;
    }

    @Override
    public IPage<MaterialListResponse> getFavorites(Long userId, int page, int size, String category, String dynasty) {
        Page<Favorite> favoritePage = new Page<>(page, size);
        IPage<Favorite> favorites = favoriteMapper.selectPage(favoritePage,
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt));

        return favorites.convert(f -> {
            Material m = materialMapper.selectById(f.getMaterialId());
            if (m == null) return null;

            List<String> tagList = m.getTags() != null && !m.getTags().isEmpty()
                    ? java.util.Arrays.asList(m.getTags().split(",")) : new java.util.ArrayList<>();

            String summary = m.getContent();
            if (summary != null) {
                summary = summary.replaceAll("<[^>]+>", "");
                if (summary.length() > 200) summary = summary.substring(0, 200);
            }

            return MaterialListResponse.builder()
                    .id(m.getId()).title(m.getTitle()).author(m.getAuthor())
                    .dynasty(m.getDynasty()).dynastyCode(m.getDynasty())
                    .category(m.getCategory()).categoryCode(m.getCategory())
                    .educationLevel(m.getEducationLevel()).educationCode(m.getEducationLevel())
                    .tags(tagList).coverImage(m.getCoverImage()).summary(summary)
                    .viewCount(m.getViewCount()).favoriteCount(m.getFavoriteCount())
                    .createdAt(m.getCreatedAt()).build();
        });
    }

    @Override
    @Transactional
    public void batchRemove(List<Long> materialIds, Long userId) {
        for (Long materialId : materialIds) {
            Favorite existing = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getUserId, userId)
                    .eq(Favorite::getMaterialId, materialId));
            if (existing != null) {
                favoriteMapper.deleteById(existing.getId());
                Material material = materialMapper.selectById(materialId);
                if (material != null) {
                    material.setFavoriteCount(Math.max(0, material.getFavoriteCount() - 1));
                    materialMapper.updateById(material);
                }
            }
        }
    }
}
