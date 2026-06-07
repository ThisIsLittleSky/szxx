package com.szxx.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名为3-20位")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码为6-20位")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "密码需包含字母和数字")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(student|teacher)$", message = "角色只能为student或teacher")
    private String role;

    private String email;
    private String phone;
}
