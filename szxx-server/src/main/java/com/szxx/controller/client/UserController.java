package com.szxx.controller.client;

import com.szxx.common.Result;
import com.szxx.dto.request.ChangePasswordRequest;
import com.szxx.dto.request.UpdateProfileRequest;
import com.szxx.dto.response.UserProfileResponse;
import com.szxx.security.SecurityContextUtil;
import com.szxx.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserProfileResponse> getProfile() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        userService.updateProfile(userId, request);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        userService.changePassword(userId, request);
        return Result.success();
    }
}
