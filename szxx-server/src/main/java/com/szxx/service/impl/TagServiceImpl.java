package com.szxx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
