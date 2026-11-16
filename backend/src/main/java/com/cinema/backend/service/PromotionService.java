package com.cinema.backend.service;

import com.cinema.backend.model.Promotion;
import com.cinema.backend.repository.PromotionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionEmailService promotionEmailService;

    public PromotionService(
            PromotionRepository promotionRepository,
            PromotionEmailService promotionEmailService
    ) {
        this.promotionRepository = promotionRepository;
        this.promotionEmailService = promotionEmailService;
    }


    public Promotion createPromotion(Promotion promotion) {

        if (
            promotion.getTitle() == null ||
            promotion.getTitle().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Promotion title is required."
            );
        }

        if (
            promotion.getDescription() == null ||
            promotion.getDescription().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Promotion description is required."
            );
        }

        if (
            promotion.getDiscount() == null ||
            promotion.getDiscount() <= 0 ||
            promotion.getDiscount() > 100
        ) {
            throw new IllegalArgumentException(
                    "Discount must be between 1 and 100."
            );
        }

        if (promotion.getExpiryDate() == null) {
            throw new IllegalArgumentException(
                    "Expiry date is required."
            );
        }

        if (
            promotion.getExpiryDate()
                    .isBefore(LocalDate.now())
        ) {
            throw new IllegalArgumentException(
                    "Expiry date cannot be in the past."
            );
        }


        Promotion savedPromotion =
                promotionRepository.save(promotion);


        promotionEmailService
                .sendPromotionToSubscribedUsers(
                        savedPromotion
                );


        return savedPromotion;
    }


    public List<Promotion> getAllPromotions() {

        return promotionRepository.findAll();
    }
}