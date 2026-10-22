package com.cinema.backend.controller;

import com.cinema.backend.dto.PaymentCardRequest;
import com.cinema.backend.dto.PaymentCardResponse;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.UserRepository;
import com.cinema.backend.service.PaymentCardService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/payment-cards")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;
    private final UserRepository userRepository;

    public PaymentCardController(
            PaymentCardService paymentCardService,
            UserRepository userRepository) {

        this.paymentCardService = paymentCardService;
        this.userRepository = userRepository;
    }

    // View the logged-in user's saved cards
    @GetMapping
    public ResponseEntity<List<PaymentCardResponse>> getCards(
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                paymentCardService.getCards(userId));
    }

    // Add a new payment card
    @PostMapping
    public ResponseEntity<PaymentCardResponse> addCard(
            Authentication authentication,
            @RequestBody PaymentCardRequest request) {

        Long userId = getAuthenticatedUserId(authentication);

        PaymentCardResponse response =
                paymentCardService.addCard(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Update an existing payment card
    @PutMapping("/{cardId}")
    public ResponseEntity<PaymentCardResponse> updateCard(
            Authentication authentication,
            @PathVariable Long cardId,
            @RequestBody PaymentCardRequest request) {

        Long userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                paymentCardService.updateCard(
                        userId, cardId, request));
    }

    // Delete a saved payment card
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            Authentication authentication,
            @PathVariable Long cardId) {

        Long userId = getAuthenticatedUserId(authentication);

        paymentCardService.deleteCard(userId, cardId);

        return ResponseEntity.noContent().build();
    }

    // Resolve the user from their authenticated identity
    private Long getAuthenticatedUserId(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login required");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User account not found"));

        return user.getId();
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