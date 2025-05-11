package com.microcredit.user.dto.response;

import java.time.LocalDateTime;

public class UserResDTO {
    public Long id;
    public String name;
    public String email;
    public LocalDateTime createdAt;
    public boolean active;

    public UserResDTO() {
    }

    public UserResDTO(Long id, String name, String email, LocalDateTime createdAt, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
