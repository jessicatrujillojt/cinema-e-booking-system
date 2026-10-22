package com.cinema.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CardEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public CardEncryptionService(
            @Value("${app.encryption.card-key}") String encodedKey) {

        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "Card encryption key must be 32 bytes");
        }

        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    // Encrypt a payment card number
    public String encrypt(String cardNumber) {

        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Card number cannot be empty");
        }

        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] encrypted = cipher.doFinal(
                    cardNumber.getBytes(StandardCharsets.UTF_8)
            );

            byte[] combined = new byte[iv.length + encrypted.length];

            System.arraycopy(
                    iv, 0, combined, 0, iv.length);

            System.arraycopy(
                    encrypted, 0, combined, iv.length,
                    encrypted.length);

            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to encrypt payment card", e);
        }
    }

    // Decrypt a payment card number when authorized
    public String decrypt(String encryptedCardNumber) {

        try {
            byte[] combined = Base64.getDecoder()
                    .decode(encryptedCardNumber);

            if (combined.length <= IV_LENGTH + TAG_LENGTH / 8) {
                throw new IllegalArgumentException(
                        "Invalid encrypted card data");
            }

            byte[] iv = new byte[IV_LENGTH];
            byte[] ciphertext =
                    new byte[combined.length - IV_LENGTH];

            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);

            System.arraycopy(
                    combined, IV_LENGTH,
                    ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] decrypted = cipher.doFinal(ciphertext);

            return new String(
                    decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to decrypt payment card", e);
        }
    }
}