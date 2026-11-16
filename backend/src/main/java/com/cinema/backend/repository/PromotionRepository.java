package com.cinema.backend.repository;

import com.cinema.backend.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionRepository
        extends JpaRepository<Promotion, Long> {

    boolean existsByPromoCodeIgnoreCase(String promoCode);

    Optional<Promotion> findByPromoCodeIgnoreCase(String promoCode);
}