package com.szxx.controller.admin;

import com.szxx.common.Result;
import com.szxx.dto.request.TagCreateRequest;
import com.szxx.entity.Tag;
import com.szxx.service.TagService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/tags")
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class AdminTagController {

    private final TagService tagService;

    @GetMapping
    public Result<List<Tag>> list() {
        return Result.success(tagService.listTagsWithCount());
    }

    @PostMapping
    public Result<Tag> create(@Valid @RequestBody TagCreateRequest request) {
        return Result.success(tagService.createTag(request.getName()));
    }

    @PutMapping("/{id}")
    public Result<Tag> update(@PathVariable Long id, @Valid @RequestBody TagCreateRequest request) {
        return Result.success(tagService.updateTag(id, request.getName()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}
