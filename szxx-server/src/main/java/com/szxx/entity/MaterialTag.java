package com.szxx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("material_tag")
public class MaterialTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long materialId;
    private Long tagId;
}
