package com.szxx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szxx.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> listTags();
    IPage<Tag> listTagsWithCount(int page, int size, String keyword);
    List<Tag> searchByName(String keyword);
    Tag createTag(String name);
    Tag updateTag(Long id, String name);
    void deleteTag(Long id);
}
