package com.cinema.backend.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TicketPricingService {

    private static final BigDecimal ADULT_PRICE =
            new BigDecimal("12.00");

    private static final BigDecimal CHILD_PRICE =
            new BigDecimal("8.00");

    private static final BigDecimal SENIOR_PRICE =
            new BigDecimal("10.00");

    public BigDecimal getAdultPrice() {
        return ADULT_PRICE;
    }

    public BigDecimal getChildPrice() {
        return CHILD_PRICE;
    }

    public BigDecimal getSeniorPrice() {
        return SENIOR_PRICE;
    }

    public BigDecimal calculateTotal(
            int adultTickets,
            int childTickets,
            int seniorTickets) {

        if (adultTickets < 0 ||
                childTickets < 0 ||
                seniorTickets < 0) {
            throw new IllegalArgumentException(
                    "Ticket quantities cannot be negative.");
        }

        BigDecimal adultTotal = ADULT_PRICE.multiply(
                BigDecimal.valueOf(adultTickets));

        BigDecimal childTotal = CHILD_PRICE.multiply(
                BigDecimal.valueOf(childTickets));

        BigDecimal seniorTotal = SENIOR_PRICE.multiply(
                BigDecimal.valueOf(seniorTickets));

        return adultTotal.add(childTotal).add(seniorTotal);
    }
}
