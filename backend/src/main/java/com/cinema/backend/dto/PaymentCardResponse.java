package com.cinema.backend.dto;

import com.cinema.backend.model.PaymentCard;

public class PaymentCardResponse {

    private Long id;
    private String lastFour;
    private String cardholderName;
    private Integer expirationMonth;
    private Integer expirationYear;

    public PaymentCardResponse(PaymentCard card) {
        this.id = card.getId();
        this.lastFour = card.getLastFour();
        this.cardholderName = card.getCardholderName();
        this.expirationMonth = card.getExpirationMonth();
        this.expirationYear = card.getExpirationYear();
    }

    public Long getId() {
        return id;
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public Integer getExpirationMonth() {
        return expirationMonth;
    }

    public Integer getExpirationYear() {
        return expirationYear;
    }
}