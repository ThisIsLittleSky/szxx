package com.szxx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.szxx.common.exception.BusinessException;
import com.szxx.dto.request.ChangePasswordRequest;
import com.szxx.dto.request.LoginRequest;
import com.szxx.dto.request.RegisterRequest;
import com.szxx.dto.request.UpdateProfileRequest;
import com.szxx.dto.response.LoginResponse;
import com.szxx.dto.response.UserProfileResponse;
import com.szxx.entity.User;
import com.szxx.mapper.UserMapper;
import com.szxx.security.JwtUtil;
import com.szxx.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public UserProfileResponse register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw BusinessException.badRequest("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole(request.getRole());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus("disabled");

        userMapper.insert(user);

        return toProfile(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        if ("disabled".equals(user.getStatus())) {
            throw BusinessException.unauthorized("该账号已被禁用，请联系管理员");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(toProfile(user))
                .build();
    }

    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.notFound("用户不存在");
        }
        return toProfile(user);
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId);
        if (request.getNickname() != null) wrapper.set(User::getNickname, request.getNickname());
        if (request.getAvatar() != null) wrapper.set(User::getAvatar, request.getAvatar());
        if (request.getPhone() != null) wrapper.set(User::getPhone, request.getPhone());
        if (request.getEmail() != null) wrapper.set(User::getEmail, request.getEmail());
        userMapper.update(wrapper);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectById(userId);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw BusinessException.badRequest("原密码错误");
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, passwordEncoder.encode(request.getNewPassword())));
    }

    private UserProfileResponse toProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
