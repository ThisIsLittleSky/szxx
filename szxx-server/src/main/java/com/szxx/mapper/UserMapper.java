package com.szxx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szxx.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
