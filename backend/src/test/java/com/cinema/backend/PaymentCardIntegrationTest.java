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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({PaymentCardService.class, CardEncryptionService.class})
@TestPropertySource(properties = {
    "app.encryption.card-key=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="
})
class PaymentCardIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository cardRepository;

    @Autowired
    private PaymentCardService cardService;

    @Autowired
    private CardEncryptionService encryptionService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("Customer");
        testUser.setEmailAddress("test@example.com");
        testUser.setPassword("test-password");
        testUser = userRepository.saveAndFlush(testUser);
    }

    private PaymentCardRequest createCard(String number) {
        PaymentCardRequest request = new PaymentCardRequest();
        request.setCardNumber(number);
        request.setCardholderName("Test Customer");

        YearMonth expiration = YearMonth.now().plusYears(2);
        request.setExpirationMonth(expiration.getMonthValue());
        request.setExpirationYear(expiration.getYear());

        return request;
    }

    @Test
    void shouldStoreEncryptedCardInH2() {
        cardService.addCard(
            testUser.getUserId(),
            createCard("4111111111111111")
        );

        List<PaymentCard> cards =
            cardRepository.findByUserUserId(testUser.getUserId());

        assertEquals(1, cards.size());

        PaymentCard stored = cards.get(0);

        assertNotEquals(
            "4111111111111111",
            stored.getEncryptedCardNumber()
        );

        assertEquals(
            "4111111111111111",
            encryptionService.decrypt(
                stored.getEncryptedCardNumber()
            )
        );

        assertEquals("1111", stored.getLastFour());
    }

    @Test
    void shouldRejectFourthCardInH2() {
        Long userId = testUser.getUserId();

        cardService.addCard(
            userId, createCard("4111111111111111"));
        cardService.addCard(
            userId, createCard("5555555555554444"));
        cardService.addCard(
            userId, createCard("378282246310005"));

        assertEquals(3, cardRepository.countByUserUserId(userId));

        assertThrows(
            IllegalArgumentException.class,
            () -> cardService.addCard(
                userId, createCard("6011111111111117"))
        );

        assertEquals(3, cardRepository.countByUserUserId(userId));
    }

    @Test
    void shouldReturnMaskedCardInformation() {
        cardService.addCard(
            testUser.getUserId(),
            createCard("4111111111111111")
        );

        List<PaymentCardResponse> cards =
            cardService.getCards(testUser.getUserId());

        assertEquals(1, cards.size());
        assertEquals("1111", cards.get(0).getLastFour());
    }
}