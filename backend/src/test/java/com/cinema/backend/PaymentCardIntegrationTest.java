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
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({PaymentCardService.class, CardEncryptionService.class})
@TestPropertySource(properties = {
    "app.encryption.card-key=${CARD_ENCRYPTION_KEY}"
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
        testUser.setEmail("test@example.com");
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
            testUser.getId(),
            createCard("4111111111111111")
        );

        List<PaymentCard> cards =
            cardRepository.findByUserId(testUser.getId());

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
        Long userId = testUser.getId();

        cardService.addCard(
            userId, createCard("4111111111111111"));
        cardService.addCard(
            userId, createCard("5555555555554444"));
        cardService.addCard(
            userId, createCard("378282246310005"));

        assertEquals(3, cardRepository.countByUserId(userId));

        assertThrows(
            IllegalArgumentException.class,
            () -> cardService.addCard(
                userId, createCard("6011111111111117"))
        );

        assertEquals(3, cardRepository.countByUserId(userId));
    }

    @Test
    void shouldReturnMaskedCardInformation() {
        cardService.addCard(
            testUser.getId(),
            createCard("4111111111111111")
        );

        List<PaymentCardResponse> cards =
            cardService.getCards(testUser.getId());

        assertEquals(1, cards.size());
        assertEquals("1111", cards.get(0).getLastFour());
    }
}