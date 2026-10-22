package com.cinema.backend.controller;

import com.cinema.backend.dto.ProfileResponse;
import com.cinema.backend.dto.UpdateProfileRequest;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.UserRepository;
import com.cinema.backend.service.ProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    public ProfileController(
            ProfileService profileService,
            UserRepository userRepository) {

        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        User user = profileService.getProfile(userId);

        return ResponseEntity.ok(new ProfileResponse(user));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {

        Long userId = getAuthenticatedUserId(authentication);

        User user = profileService.updateProfile(userId, request);

        return ResponseEntity.ok(new ProfileResponse(user));
    }

    private Long getAuthenticatedUserId(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new org.springframework.web.server
                    .ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Login required");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() ->
                    new org.springframework.web.server
                        .ResponseStatusException(
                            org.springframework.http.HttpStatus.UNAUTHORIZED,
                            "User account not found"));
    }
}