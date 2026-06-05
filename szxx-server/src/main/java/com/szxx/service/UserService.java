package com.szxx.service;

import com.szxx.dto.request.ChangePasswordRequest;
import com.szxx.dto.request.LoginRequest;
import com.szxx.dto.request.RegisterRequest;
import com.szxx.dto.request.UpdateProfileRequest;
import com.szxx.dto.response.LoginResponse;
import com.szxx.dto.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    UserProfileResponse getProfile(Long userId);
    void updateProfile(Long userId, UpdateProfileRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
}
