package com.cinema.backend;

import com.cinema.backend.dto.PaymentCardRequest;
import com.cinema.backend.dto.PaymentCardResponse;
import com.cinema.backend.model.PaymentCard;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.PaymentCardRepository;
import com.cinema.backend.repository.UserRepository;
import com.cinema.backend.service.CardEncryptionService;
import com.cinema.backend.service.PaymentCardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.YearMonth;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentCardServiceTest {

    private PaymentCardRepository cardRepository;
    private UserRepository userRepository;
    private CardEncryptionService encryptionService;
    private PaymentCardService paymentCardService;

    private User testUser;

    @BeforeEach
    void setUp() {

        cardRepository = mock(PaymentCardRepository.class);
        userRepository = mock(UserRepository.class);

        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);

        encryptionService = new CardEncryptionService(
                Base64.getEncoder().encodeToString(key));

        paymentCardService = new PaymentCardService(
                cardRepository,
                userRepository,
                encryptionService);

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmailAddress("test@example.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        when(cardRepository.save(any(PaymentCard.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));
    }

    private PaymentCardRequest validCard() {

        PaymentCardRequest request = new PaymentCardRequest();

        request.setCardNumber("4111111111111111");
        request.setCardholderName("Test User");

        YearMonth expiration = YearMonth.now().plusYears(2);

        request.setExpirationMonth(expiration.getMonthValue());
        request.setExpirationYear(expiration.getYear());

        return request;
    }

    @Test
    void shouldAddCardWhenUserHasLessThanThree() {

        when(cardRepository.countByUserUserId(1L))
                .thenReturn(2L);

        PaymentCardResponse response =
                paymentCardService.addCard(1L, validCard());

        assertNotNull(response);
        assertEquals("1111", response.getLastFour());

        verify(cardRepository).save(any(PaymentCard.class));
    }

    @Test
    void shouldRejectFourthCard() {

        when(cardRepository.countByUserUserId(1L))
                .thenReturn(3L);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> paymentCardService.addCard(
                                1L, validCard()));

        assertEquals(
                "You cannot save more than 3 payment cards",
                exception.getMessage());

        verify(cardRepository, never())
                .save(any(PaymentCard.class));
    }

    @Test
    void shouldEncryptCardNumberBeforeSaving() {

        when(cardRepository.countByUserUserId(1L))
                .thenReturn(0L);

        paymentCardService.addCard(1L, validCard());

        ArgumentCaptor<PaymentCard> captor =
                ArgumentCaptor.forClass(PaymentCard.class);

        verify(cardRepository).save(captor.capture());

        PaymentCard savedCard = captor.getValue();

        assertNotEquals(
                "4111111111111111",
                savedCard.getEncryptedCardNumber());

        assertEquals(
                "4111111111111111",
                encryptionService.decrypt(
                        savedCard.getEncryptedCardNumber()));

        assertEquals("1111", savedCard.getLastFour());
    }

    @Test
    void shouldReturnOnlyMaskedCardInformation() {

        PaymentCard card = new PaymentCard();
        card.setLastFour("1111");
        card.setCardholderName("Test User");
        card.setExpirationMonth(12);
        card.setExpirationYear(2030);
        card.setEncryptedCardNumber(
                encryptionService.encrypt("4111111111111111"));

        when(cardRepository.findByUserUserId(1L))
                .thenReturn(List.of(card));

        List<PaymentCardResponse> responses =
                paymentCardService.getCards(1L);

        assertEquals(1, responses.size());
        assertEquals("1111", responses.get(0).getLastFour());

        assertFalse(
                java.util.Arrays.stream(
                        PaymentCardResponse.class.getMethods())
                        .anyMatch(method ->
                                method.getName().equals(
                                        "getEncryptedCardNumber")
                                || method.getName().equals(
                                        "getCardNumber")));
    }
}