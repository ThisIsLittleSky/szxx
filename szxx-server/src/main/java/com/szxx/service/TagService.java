package com.szxx.service;

import com.szxx.entity.Tag;

import java.util.List;
import java.util.Map;

public interface TagService {
    List<Tag> listTags();
    List<Tag> listTagsWithCount();
    List<Tag> searchByName(String keyword);
    Tag createTag(String name);
    Tag updateTag(Long id, String name);
    void deleteTag(Long id);
}
