package com.cinema.backend.service;

import com.cinema.backend.dto.UpdateProfileRequest;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileNotificationService notificationService;

    // Constructor
    public ProfileService(
            UserRepository userRepository,
            ProfileNotificationService notificationService) {

        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // Retrieve a user's existing profile
    @Transactional(readOnly = true)
    public User getProfile(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found"));
    }

    // Update editable profile information
    @Transactional
    public User updateProfile(
            Long userId,
            UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found"));

        // Validate required fields
        if (request == null) {
            throw new IllegalArgumentException(
                    "Profile information is required");
        }

        if (request.getFirstName() == null ||
                request.getFirstName().isBlank()) {
            throw new IllegalArgumentException(
                    "First name is required");
        }

        if (request.getLastName() == null ||
                request.getLastName().isBlank()) {
            throw new IllegalArgumentException(
                    "Last name is required");
        }

        // Update personal information
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());

        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setZipCode(request.getZipCode());

        if (request.getSubscribedToPromotions() != null) {
            user.setSubscribedToPromotions(
                    request.getSubscribedToPromotions());
        }

        // Email, password, role, and status are NOT changed

        // Save updated profile to the database
        User savedUser = userRepository.save(user);

        // Send profile update confirmation email
        notificationService.sendProfileUpdatedEmail(
                savedUser.getEmail(),
                savedUser.getFirstName()
        );

        return savedUser;
    }
}