package com.szxx.controller.common;

import com.szxx.common.Result;
import com.szxx.entity.Tag;
import com.szxx.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public Result<List<Tag>> list(@RequestParam(required = false) String keyword) {
        return Result.success(tagService.searchByName(keyword));
    }
}
