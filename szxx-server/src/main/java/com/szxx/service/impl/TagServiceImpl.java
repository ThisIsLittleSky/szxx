package com.szxx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szxx.common.exception.BusinessException;
import com.szxx.entity.MaterialTag;
import com.szxx.entity.Tag;
import com.szxx.mapper.MaterialTagMapper;
import com.szxx.mapper.TagMapper;
import com.szxx.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final MaterialTagMapper materialTagMapper;

    @Override
    public List<Tag> listTags() {
        return tagMapper.selectList(new LambdaQueryWrapper<Tag>().orderByAsc(Tag::getName));
    }

    @Override
    public IPage<Tag> listTagsWithCount(int page, int size, String keyword) {
        List<Tag> all = tagMapper.listTagsWithCount();

        // 关键词过滤
        if (keyword != null && !keyword.isBlank()) {
            all = all.stream()
                    .filter(t -> t.getName() != null && t.getName().contains(keyword))
                    .collect(Collectors.toList());
        }

        // 手动分页
        long total = all.size();
        int fromIndex = (page - 1) * size;
        if (fromIndex >= total) {
            return new Page<Tag>(page, size, total).setRecords(List.of());
        }
        int toIndex = Math.min(fromIndex + size, (int) total);
        List<Tag> pageRecords = all.subList(fromIndex, toIndex);

        return new Page<Tag>(page, size, total).setRecords(pageRecords);
    }

    @Override
    public List<Tag> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listTags();
        }
        return tagMapper.searchByName(keyword);
    }

    @Override
    public Tag createTag(String name) {
        Long count = tagMapper.selectCount(new LambdaQueryWrapper<Tag>().eq(Tag::getName, name));
        if (count > 0) {
            throw BusinessException.badRequest("标签名称已存在");
        }
        Tag tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public Tag updateTag(Long id, String name) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw BusinessException.notFound("标签不存在");
        }
        tag.setName(name);
        tagMapper.updateById(tag);
        return tag;
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw BusinessException.notFound("标签不存在");
        }
        materialTagMapper.delete(new LambdaQueryWrapper<MaterialTag>().eq(MaterialTag::getTagId, id));
        tagMapper.deleteById(id);
    }
}
