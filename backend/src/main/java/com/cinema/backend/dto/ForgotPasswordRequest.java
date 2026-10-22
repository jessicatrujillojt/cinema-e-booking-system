package com.cinema.backend.dto;

public class ForgotPasswordRequest {

    private String emailAddress;

    public ForgotPasswordRequest() {}

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}