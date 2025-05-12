package com.microcredit.user.controller.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microcredit.user.dto.request.LoginReqDTO;
import com.microcredit.user.dto.response.LoginResDTO;
import com.microcredit.user.security.JwtUtil;
import com.microcredit.user.service.AuthService;
import com.microcredit.user.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthService authService;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    CustomUserDetailsService customUserDetailsService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testLoginSuccess() throws Exception {
        var req = new LoginReqDTO();
        req.setEmail("u@u.com");
        req.setPassword("secret");

        var resp = new LoginResDTO("jwt-token-123");
        given(authService.login(any(LoginReqDTO.class))).willReturn(resp);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    void testLoginFailure() throws Exception {
        var req = new LoginReqDTO();
        req.setEmail("u@u.com");
        req.setPassword("wrong");

        given(authService.login(any()))
                .willThrow(new BadCredentialsException("invalid"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
