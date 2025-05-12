package com.microcredit.user.service;

import com.microcredit.user.dto.request.LoginReqDTO;
import com.microcredit.user.dto.response.LoginResDTO;
import com.microcredit.user.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    AuthenticationManager authManager;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    CustomUserDetailsService uds;
    @InjectMocks
    AuthService authService;

    private LoginReqDTO req;

    @BeforeEach
    void setup() {
        req = new LoginReqDTO();
        req.setEmail("u@u.com");
        req.setPassword("pwd");
    }

    @Test
    void loginSuccess() {
        // organiza
        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername("u@u.com")
                .password("pwd")
                .roles("USER")
                .build();

        // Simula o AuthenticationManager retornando um token autenticado
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        user.getAuthorities()
                );
        given(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authToken);

        // Simula o carregamento do UserDetails e a geração do token
        given(uds.loadUserByUsername("u@u.com")).willReturn(user);
        given(jwtUtil.generateToken(user)).willReturn("tok-123");

        // executa
        LoginResDTO resp = authService.login(req);

        // verifica
        assertEquals("tok-123", resp.getToken());
    }


    @Test
    void loginBadCredentials() {
        willThrow(new BadCredentialsException("Invalid"))
                .given(authManager)
                .authenticate(any());
        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }
}
