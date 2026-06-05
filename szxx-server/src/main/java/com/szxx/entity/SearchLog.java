package com.szxx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("search_log")
public class SearchLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String keyword;
    private Integer resultCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
