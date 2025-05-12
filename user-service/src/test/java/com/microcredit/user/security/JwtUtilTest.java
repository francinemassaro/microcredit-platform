package com.microcredit.user.security;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // precisa ser pelo menos 256 bits (32 bytes)
        String secret = "01234567890123456789012345678901";
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600_000L); // 1h
    }

    @Test
    void generateToken_extractUsername_and_validateToken_success() {
        var user = User.withUsername("john@example.com")
                .password("irrelevant")
                .roles("USER")
                .build();

        String token = jwtUtil.generateToken(user);
        assertNotNull(token);
        // subject
        assertEquals("john@example.com", jwtUtil.extractUsername(token));
        // deve ser válido
        assertTrue(jwtUtil.validateToken(token, user));
    }

    @Test
    void validateToken_fails_when_username_mismatch() {
        var user1 = User.withUsername("user1@example.com")
                .password("x")
                .roles("USER")
                .build();
        var user2 = User.withUsername("user2@example.com")
                .password("x")
                .roles("USER")
                .build();

        String token = jwtUtil.generateToken(user1);
        // username diferente → retorna false sem nem checar expiração
        assertFalse(jwtUtil.validateToken(token, user2));
    }
}
