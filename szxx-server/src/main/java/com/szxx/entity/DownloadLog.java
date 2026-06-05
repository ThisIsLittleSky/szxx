package com.szxx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("download_log")
public class DownloadLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long materialId;
    private String downloadType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
