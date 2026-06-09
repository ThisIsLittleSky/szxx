package com.szxx.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("material")
public class Material {

    @JsonSerialize(using = ToStringSerializer.class)
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

    @TableField(exist = false)
    private String uploaderName;

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
