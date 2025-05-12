package com.microcredit.user.controller.external;

import com.microcredit.user.dto.request.LoginReqDTO;
import com.microcredit.user.dto.response.LoginResDTO;
import com.microcredit.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResDTO> login(
            @RequestBody @Valid LoginReqDTO request) {
        LoginResDTO resp = authService.login(request);
        return ResponseEntity.ok(resp);
    }
}
