package com.cinema.backend.service;

import com.cinema.backend.model.Customer;
import com.cinema.backend.model.Promotion;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.CustomerRepository;
import com.cinema.backend.repository.UserRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionEmailService {

    private final JavaMailSender mailSender;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public PromotionEmailService(
            JavaMailSender mailSender,
            CustomerRepository customerRepository,
            UserRepository userRepository
    ) {
        this.mailSender = mailSender;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }


    public void sendPromotionToSubscribedUsers(
            Promotion promotion
    ) {

        List<Customer> subscribedCustomers =
                customerRepository
                        .findByPromotionSubscriptionsTrue();


        for (Customer customer : subscribedCustomers) {

            User user =
                    userRepository
                            .findById(customer.getUserId())
                            .orElse(null);

            if (user == null) {
                continue;
            }


            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setTo(
                    user.getEmailAddress()
            );

            message.setSubject(
                    "13th Reel Promotion: "
                            + promotion.getTitle()
            );

            message.setText(
                    "Hi "
                            + user.getFirstName()
                            + ",\n\n"
                            + promotion.getDescription()
                            + "\n\n"
                            + "Promo Code: "
                            + promotion.getPromoCode()
                            + "\n"
                            + "Discount: "
                            + promotion.getDiscount()
                            + "%\n"
                            + "Expires: "
                            + promotion.getExpiryDate()
                            + "\n\n"
                            + "Thank you,\n"
                            + "13th Reel"
            );


            mailSender.send(message);
        }
    }
}