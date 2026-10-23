package com.cinema.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {

    @Id
    private Long userId;

    public Admin() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}