package com.cinema.backend.service;

import com.cinema.backend.dto.LoginResult;
import com.cinema.backend.dto.RegisterRequest;
import com.cinema.backend.model.Customer;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.AdminRepository;
import com.cinema.backend.repository.CustomerRepository;
import com.cinema.backend.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            AdminRepository adminRepository) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
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
        customer.setVerificationToken(UUID.randomUUID().toString());
        customer.setPromotionSubscriptions(
                request.isPromotionSubscriptions()
        );

        customerRepository.save(customer);

        return savedUser;
    }

    @Transactional
    public void verifyAccount(String token) {

        Customer customer = customerRepository.findByVerificationToken(token)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid verification token."
                        )
                );

        customer.setStatus("ACTIVE");
        customer.setVerificationToken(null);

        customerRepository.save(customer);
    }

    public LoginResult login(String emailAddress, String password) {

        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password."
                        )
                );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid email or password."
            );
        }

        if (adminRepository.existsById(user.getUserId())) {
            return new LoginResult(
                    user.getUserId(),
                    "ADMIN"
            );
        }

        Customer customer = customerRepository.findById(user.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer account not found."
                        )
                );

        if (!"ACTIVE".equals(customer.getStatus())) {
            throw new IllegalArgumentException(
                    "Account is not verified. Please verify your account before logging in."
            );
        }

        return new LoginResult(
                user.getUserId(),
                "CUSTOMER"
        );
    }

    @Transactional
public String requestPasswordReset(String emailAddress) {

    User user = userRepository.findByEmailAddress(emailAddress)
            .orElseThrow(() ->
                    new IllegalArgumentException("No account found with this email.")
            );

    String resetToken = UUID.randomUUID().toString();

    user.setResetPasswordToken(resetToken);

    userRepository.save(user);

    return resetToken;
}

@Transactional
public void resetPassword(String token, String newPassword) {

    User user = userRepository.findByResetPasswordToken(token)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Invalid password reset token."
                    )
            );

    String hashedPassword =
            passwordEncoder.encode(newPassword);

    user.setPassword(hashedPassword);
    user.setResetPasswordToken(null);

    userRepository.save(user);
}
}