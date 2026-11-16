package com.cinema.backend.repository;

import com.cinema.backend.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository
        extends JpaRepository<Promotion, Long> {
}