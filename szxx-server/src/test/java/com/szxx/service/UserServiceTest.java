package com.szxx.service;

import com.szxx.common.exception.BusinessException;
import com.szxx.dto.request.LoginRequest;
import com.szxx.dto.request.RegisterRequest;
import com.szxx.dto.response.LoginResponse;
import com.szxx.dto.response.UserProfileResponse;
import com.szxx.entity.User;
import com.szxx.mapper.UserMapper;
import com.szxx.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private com.szxx.service.impl.UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("Abc12345");
        registerRequest.setNickname("测试用户");
        registerRequest.setRole("student");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("encoded_password");
        mockUser.setNickname("测试用户");
        mockUser.setRole("student");
        mockUser.setStatus("active");
    }

    @Test
    void testRegisterSuccess() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode("Abc12345")).thenReturn("encoded_password");
        when(userMapper.insert(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return 1;
        });

        UserProfileResponse result = userService.register(registerRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("student", result.getRole());
    }

    @Test
    void testRegisterDuplicateUsername() {
        when(userMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> userService.register(registerRequest));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("Abc12345");

        when(userMapper.selectOne(any())).thenReturn(mockUser);
        when(passwordEncoder.matches("Abc12345", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "testuser", "student")).thenReturn("test_jwt_token");

        LoginResponse result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals("test_jwt_token", result.getToken());
    }

    @Test
    void testLoginWrongPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("WrongPassword");

        when(userMapper.selectOne(any())).thenReturn(mockUser);
        when(passwordEncoder.matches("WrongPassword", "encoded_password")).thenReturn(false);

        assertThrows(BusinessException.class, () -> userService.login(loginRequest));
    }
}
