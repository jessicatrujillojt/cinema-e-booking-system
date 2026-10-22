package com.cinema.backend.controller;

import com.cinema.backend.dto.ProfileResponse;
import com.cinema.backend.dto.UpdateProfileRequest;
import com.cinema.backend.model.Customer;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.CustomerRepository;
import com.cinema.backend.service.ProfileService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final CustomerRepository customerRepository;

    public ProfileController(
            ProfileService profileService,
            CustomerRepository customerRepository) {

        this.profileService = profileService;
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            HttpSession session) {

        Long userId = getAuthenticatedUserId(session);

        User user = profileService.getProfile(userId);

        Customer customer = customerRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Customer not found"));

        return ResponseEntity.ok(
                new ProfileResponse(
                        user,
                        customer.isPromotionSubscriptions()));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            HttpSession session,
            @RequestBody UpdateProfileRequest request) {

        Long userId = getAuthenticatedUserId(session);

        User user =
                profileService.updateProfile(
                        userId,
                        request);

        Customer customer = customerRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Customer not found"));

        return ResponseEntity.ok(
                new ProfileResponse(
                        user,
                        customer.isPromotionSubscriptions()));
    }

    private Long getAuthenticatedUserId(
            HttpSession session) {

        Object userId =
                session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login required");
        }

        return (Long) userId;
    }
}