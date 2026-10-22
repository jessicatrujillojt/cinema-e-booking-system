package com.cinema.backend.service;

import com.cinema.backend.dto.RegisterRequest;
import com.cinema.backend.model.Customer;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.CustomerRepository;
import com.cinema.backend.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            CustomerRepository customerRepository) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public User registerCustomer(RegisterRequest request) {

        if (userRepository.existsByEmailAddress(request.getEmailAddress())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmailAddress(request.getEmailAddress());
        user.setPhoneNumber(request.getPhoneNumber());

        String hashedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);

        Customer customer = new Customer();

        customer.setUserId(savedUser.getUserId());
        customer.setStatus("INACTIVE");
        customer.setPromotionSubscriptions(
                request.isPromotionSubscriptions()
        );

        customerRepository.save(customer);

        return savedUser;
    }
}