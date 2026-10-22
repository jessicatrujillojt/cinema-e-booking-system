package com.cinema.backend.repository;

import com.cinema.backend.model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentCardRepository
        extends JpaRepository<PaymentCard, Long> {

    // Retrieve cards belonging to a specific user
    List<PaymentCard> findByUserId(Long userId);

    // Count cards belonging to a specific user
    long countByUserId(Long userId);

}