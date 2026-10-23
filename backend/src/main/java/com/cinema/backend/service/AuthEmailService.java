package com.cinema.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AuthEmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public AuthEmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String fromAddress) {

        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendVerificationEmail(
            String email,
            String firstName,
            String verificationToken) {

        String verificationLink =
                "http://localhost:8080/api/auth/verify?token="
                        + verificationToken;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject(
                "Cinema E-Booking System - Verify Your Account");

        message.setText(
                "Hello " + firstName + ",\n\n" +
                "Thank you for creating an account.\n\n" +
                "Please verify your account using the link below:\n" +
                verificationLink + "\n\n" +
                "Cinema E-Booking System"
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(
            String email,
            String firstName,
            String resetToken) {

        String resetLink =
                "http://localhost:5501/reset-password.html?token="
                        + resetToken;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject(
                "Cinema E-Booking System - Password Reset");

        message.setText(
                "Hello " + firstName + ",\n\n" +
                "A password reset was requested for your account.\n\n" +
                "Use the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "If you did not request a password reset, " +
                "you can ignore this email.\n\n" +
                "Cinema E-Booking System"
        );

        mailSender.send(message);
    }
}