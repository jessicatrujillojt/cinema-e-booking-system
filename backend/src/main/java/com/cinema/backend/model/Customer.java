package com.cinema.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    private Long userId;

    @Column(nullable = false)
    private String status;

    private String preferences;

    private boolean promotionSubscriptions;

    public Customer() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public boolean isPromotionSubscriptions() {
        return promotionSubscriptions;
    }

    public void setPromotionSubscriptions(boolean promotionSubscriptions) {
        this.promotionSubscriptions = promotionSubscriptions;
    }
}