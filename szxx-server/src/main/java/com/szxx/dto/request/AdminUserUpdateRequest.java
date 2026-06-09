package com.szxx.dto.request;

import lombok.Data;

@Data
public class AdminUserUpdateRequest {
    private String role;
    private String status;
    private String password;
}
