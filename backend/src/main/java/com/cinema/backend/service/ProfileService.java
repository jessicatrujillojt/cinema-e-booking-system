package com.cinema.backend.service;

import com.cinema.backend.dto.UpdateProfileRequest;
import com.cinema.backend.model.Customer;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.CustomerRepository;
import com.cinema.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProfileNotificationService notificationService;

    public ProfileService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            ProfileNotificationService notificationService) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
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

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());

        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setZipCode(request.getZipCode());

        if (request.getSubscribedToPromotions() != null) {

            Customer customer = customerRepository.findById(userId)
                    .orElseThrow(() ->
                            new NoSuchElementException(
                                    "Customer not found"));

            customer.setPromotionSubscriptions(
                    request.getSubscribedToPromotions());

            customerRepository.save(customer);
        }

        User savedUser = userRepository.save(user);

        notificationService.sendProfileUpdatedEmail(
                savedUser.getEmailAddress(),
                savedUser.getFirstName()
        );

        return savedUser;
    }
}