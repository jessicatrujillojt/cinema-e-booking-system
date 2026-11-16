package com.cinema.backend.controller;

import com.cinema.backend.model.Promotion;
import com.cinema.backend.service.PromotionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(
        origins = "http://localhost:5501",
        allowCredentials = "true"
)
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(
            PromotionService promotionService
    ) {
        this.promotionService = promotionService;
    }


    @GetMapping
    public ResponseEntity<List<Promotion>>
    getAllPromotions() {

        return ResponseEntity.ok(
                promotionService.getAllPromotions()
        );
    }


    @PostMapping
    public ResponseEntity<?> createPromotion(
            @RequestBody Promotion promotion
    ) {

        try {

            Promotion savedPromotion =
                    promotionService
                            .createPromotion(promotion);

            return ResponseEntity.ok(
                    savedPromotion
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }
    @GetMapping("/validate/{promoCode}")
        public ResponseEntity<?> validatePromoCode(
                @PathVariable String promoCode
        ) {
        try {

                Promotion promotion =
                        promotionService
                                .validatePromoCode(
                                        promoCode
                                );

                return ResponseEntity.ok(
                        promotion
                );

        } catch (IllegalArgumentException e) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "error",
                                        e.getMessage()
                                )
                        );
        }
        }
}