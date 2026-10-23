package com.cinema.backend.dto;

import com.cinema.backend.model.User;

public class ProfileResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private boolean subscribedToPromotions;

    public ProfileResponse(User user) {
        this(user, false);
    }

    public ProfileResponse(
            User user,
            boolean subscribedToPromotions) {

        this.id = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmailAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.city = user.getCity();
        this.state = user.getState();
        this.zipCode = user.getZipCode();
        this.subscribedToPromotions =
                subscribedToPromotions;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public boolean isSubscribedToPromotions() {
        return subscribedToPromotions;
    }
}