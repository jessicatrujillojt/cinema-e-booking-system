package com.cinema.backend.controller;

import com.cinema.backend.dto.PaymentCardRequest;
import com.cinema.backend.dto.PaymentCardResponse;
import com.cinema.backend.service.PaymentCardService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/payment-cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController(
            PaymentCardService paymentCardService) {

        this.paymentCardService = paymentCardService;
    }

    // View the logged-in user's saved cards
    @GetMapping
    public ResponseEntity<List<PaymentCardResponse>> getCards(
            HttpSession session) {

        Long userId = getAuthenticatedUserId(session);

        return ResponseEntity.ok(
                paymentCardService.getCards(userId));
    }

    // Add a new payment card
    @PostMapping
    public ResponseEntity<PaymentCardResponse> addCard(
            HttpSession session,
            @RequestBody PaymentCardRequest request) {

        Long userId = getAuthenticatedUserId(session);

        PaymentCardResponse response =
                paymentCardService.addCard(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Update an existing payment card
    @PutMapping("/{cardId}")
    public ResponseEntity<PaymentCardResponse> updateCard(
            HttpSession session,
            @PathVariable Long cardId,
            @RequestBody PaymentCardRequest request) {

        Long userId = getAuthenticatedUserId(session);

        return ResponseEntity.ok(
                paymentCardService.updateCard(
                        userId, cardId, request));
    }

    // Delete a saved payment card
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            HttpSession session,
            @PathVariable Long cardId) {

        Long userId = getAuthenticatedUserId(session);

        paymentCardService.deleteCard(userId, cardId);

        return ResponseEntity.noContent().build();
    }

    // Identify the logged-in user from the session
    private Long getAuthenticatedUserId(
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login required");
        }

        return (Long) userId;
    }

    // Return an appropriate error for invalid card data
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidCard(
            IllegalArgumentException exception) {

        return ResponseEntity
                .badRequest()
                .body(exception.getMessage());
    }
}