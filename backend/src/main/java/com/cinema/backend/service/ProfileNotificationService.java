package com.cinema.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ProfileNotificationService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public ProfileNotificationService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendProfileUpdatedEmail(String email, String firstName) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("Cinema E-Booking System - Profile Updated");

        message.setText(
                "Hello " + firstName + ",\n\n" +
                "Your Cinema E-Booking System profile information " +
                "has been updated successfully.\n\n" +
                "If you did not make this change, please contact support.\n\n" +
                "Cinema E-Booking System"
        );

        mailSender.send(message);
    }
}