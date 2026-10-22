package com.cinema.backend.service;

import com.cinema.backend.dto.PaymentCardRequest;
import com.cinema.backend.dto.PaymentCardResponse;
import com.cinema.backend.model.PaymentCard;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.PaymentCardRepository;
import com.cinema.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PaymentCardService {

    private final PaymentCardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardEncryptionService encryptionService;

    public PaymentCardService(
            PaymentCardRepository cardRepository,
            UserRepository userRepository,
            CardEncryptionService encryptionService) {

        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    // View saved payment cards
    @Transactional(readOnly = true)
    public List<PaymentCardResponse> getCards(Long userId) {

        return cardRepository.findByUserUserId(userId)
                .stream()
                .map(PaymentCardResponse::new)
                .toList();
    }

    // Add a payment card
    @Transactional
    public PaymentCardResponse addCard(
            Long userId,
            PaymentCardRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found"));

        // Maximum 3 cards per user
        if (cardRepository.countByUserUserId(userId) >= 3) {
            throw new IllegalArgumentException(
                    "You cannot save more than 3 payment cards");
        }

        String cardNumber = validateCard(request);

        PaymentCard card = new PaymentCard();

        card.setUser(user);
        card.setCardholderName(
                request.getCardholderName().trim());

        card.setExpirationMonth(request.getExpirationMonth());
        card.setExpirationYear(request.getExpirationYear());

        // Save only the last four digits for display
        card.setLastFour(
                cardNumber.substring(cardNumber.length() - 4));

        // Encrypt the full card number before storage
        card.setEncryptedCardNumber(
                encryptionService.encrypt(cardNumber));

        PaymentCard saved = cardRepository.save(card);

        return new PaymentCardResponse(saved);
    }

    // Update an existing payment card
    @Transactional
    public PaymentCardResponse updateCard(
            Long userId,
            Long cardId,
            PaymentCardRequest request) {

        PaymentCard card = getOwnedCard(userId, cardId);

        String cardNumber = validateCard(request);

        card.setCardholderName(
                request.getCardholderName().trim());

        card.setExpirationMonth(request.getExpirationMonth());
        card.setExpirationYear(request.getExpirationYear());

        card.setLastFour(
                cardNumber.substring(cardNumber.length() - 4));

        card.setEncryptedCardNumber(
                encryptionService.encrypt(cardNumber));

        return new PaymentCardResponse(cardRepository.save(card));
    }

    // Delete a saved payment card
    @Transactional
    public void deleteCard(Long userId, Long cardId) {

        PaymentCard card = getOwnedCard(userId, cardId);

        cardRepository.delete(card);
    }

    // Ensure users only access their own cards
    private PaymentCard getOwnedCard(Long userId, Long cardId) {

        PaymentCard card = cardRepository.findById(cardId)
                .orElseThrow(() ->
                        new NoSuchElementException("Card not found"));

        if (!card.getUser().getUserId().equals(userId)) {
            throw new NoSuchElementException("Card not found");
        }

        return card;
    }

    // Validate payment card information
    private String validateCard(PaymentCardRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Payment card information is required");
        }

        String number = request.getCardNumber();

        if (number == null) {
            throw new IllegalArgumentException(
                    "Card number is required");
        }

        number = number.replaceAll("[ -]", "");

        if (!number.matches("\\d{13,19}")) {
            throw new IllegalArgumentException(
                    "Card number must contain 13 to 19 digits");
        }

        if (request.getCardholderName() == null ||
                request.getCardholderName().isBlank()) {
            throw new IllegalArgumentException(
                    "Cardholder name is required");
        }

        Integer month = request.getExpirationMonth();
        Integer year = request.getExpirationYear();

        if (month == null || month < 1 || month > 12 ||
                year == null || year < 2000 || year > 9999) {
            throw new IllegalArgumentException(
                    "Invalid expiration date");
        }

        YearMonth expiration = YearMonth.of(year, month);

        if (expiration.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException(
                    "Payment card has expired");
        }

        return number;
    }
}