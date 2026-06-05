package com.szxx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("material")
public class Material {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;
    private String author;
    private String dynasty;
    private String category;
    private String educationLevel;
    private String tags;
    private String coverImage;
    private String summary;
    private String content;
    private String videoUrl;
    private Long uploaderId;
    private String status;
    private String reviewComment;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer downloadCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
