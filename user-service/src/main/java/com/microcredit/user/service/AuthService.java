package com.microcredit.user.service;

import com.microcredit.user.dto.request.LoginReqDTO;
import com.microcredit.user.dto.response.LoginResDTO;
import com.microcredit.user.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(AuthenticationManager authManager,
                       JwtUtil jwtUtil,
                       CustomUserDetailsService userDetailsService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public LoginResDTO login(LoginReqDTO dto) {
        // 1) Tenta autenticar
        var authToken =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        authManager.authenticate(authToken);

        // 2) Carrega UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());

        // 3) Gera token
        String token = jwtUtil.generateToken(userDetails);

        // 4) Retorna DTO
        return new LoginResDTO(token);
    }
}

