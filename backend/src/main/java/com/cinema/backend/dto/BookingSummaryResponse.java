package com.cinema.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record BookingSummaryResponse(

        Long bookingId,

        String movieTitle,

        LocalDate showDate,

        LocalTime showTime,

        String showroomName,

        List<String> selectedSeats,

        int adultTickets,

        int childTickets,

        int seniorTickets,

        int totalTickets,

        BigDecimal adultTicketPrice,

        BigDecimal childTicketPrice,

        BigDecimal seniorTicketPrice,

        BigDecimal totalBeforeTax,

        String emailAddress,

        String status

) {
}
