package com.microcredit.user.dto.response;

public class LoginResDTO {
    private String token;

    public LoginResDTO() {
    }

    public LoginResDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
