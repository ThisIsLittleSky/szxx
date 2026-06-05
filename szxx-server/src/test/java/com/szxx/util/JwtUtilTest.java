package com.szxx.util;

import com.szxx.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil(
            "U3p4eFN5c3RlbTIwMjZKV1RTZWNyZXRLZXlGb3JIUzI1NlRva2VuR2VuZXJhdGlvbjI1NkJpdExlbmd0aCEh",
            86400000L);

    @Test
    void testGenerateAndParse() {
        String token = jwtUtil.generateToken(1L, "testuser", "student");
        assertNotNull(token);

        Claims claims = jwtUtil.parseToken(token);
        assertEquals("1", claims.getSubject());
        assertEquals("testuser", claims.get("username", String.class));
        assertEquals("student", claims.get("role", String.class));
    }

    @Test
    void testValidateValidToken() {
        String token = jwtUtil.generateToken(1L, "testuser", "student");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    @Test
    void testGetUserId() {
        String token = jwtUtil.generateToken(42L, "testuser", "student");
        assertEquals(42L, jwtUtil.getUserIdFromToken(token));
    }
}
