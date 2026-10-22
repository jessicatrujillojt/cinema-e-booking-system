package com.cinema.backend;

import com.cinema.backend.service.CardEncryptionService;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class CardEncryptionServiceTest {

    @Test
    void testCardEncryptionAndDecryption() {

        // Generate a temporary test key
        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);

        String encodedKey =
                Base64.getEncoder().encodeToString(key);

        CardEncryptionService encryptionService =
                new CardEncryptionService(encodedKey);

        // Synthetic test card number only
        String originalCardNumber = "4111111111111111";

        // Encrypt the card number
        String encrypted =
                encryptionService.encrypt(originalCardNumber);

        // Verify the stored value is not plaintext
        assertNotEquals(originalCardNumber, encrypted);

        // Verify the card number can be decrypted
        String decrypted =
                encryptionService.decrypt(encrypted);

        assertEquals(originalCardNumber, decrypted);
    }

    @Test
    void testDifferentEncryptionsProduceDifferentResults() {

        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);

        CardEncryptionService encryptionService =
                new CardEncryptionService(
                        Base64.getEncoder().encodeToString(key));

        String cardNumber = "4111111111111111";

        String encrypted1 = encryptionService.encrypt(cardNumber);
        String encrypted2 = encryptionService.encrypt(cardNumber);

        // Random IVs should produce different ciphertext
        assertNotEquals(encrypted1, encrypted2);
    }
}