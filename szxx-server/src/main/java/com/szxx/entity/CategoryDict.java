package com.szxx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("category_dict")
public class CategoryDict {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String type;
    private String code;
    private String name;
    private Integer sortOrder;
}
