package com.cinema.backend.dto;

public class LoginResult {

    private Long userId;
    private String role;

    public LoginResult(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}