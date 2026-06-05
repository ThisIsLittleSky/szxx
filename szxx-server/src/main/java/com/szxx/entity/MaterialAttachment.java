package com.szxx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("material_attachment")
public class MaterialAttachment {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long materialId;
    private String filename;
    private String filePath;
    private String fileType;
    private Long fileSize;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
