
package com.cinema.backend.controller;

import com.cinema.backend.dto.BookingSummaryResponse;
import com.cinema.backend.model.Booking;
import com.cinema.backend.model.BookingSeat;
import com.cinema.backend.service.BookingService;
import com.cinema.backend.repository.BookingSeatRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final String BOOKING_TOKEN =
            "bookingOwnerToken";

    private final BookingService bookingService;
    private final BookingSeatRepository bookingSeatRepository;

    public BookingController(
            BookingService bookingService,
            BookingSeatRepository bookingSeatRepository) {

        this.bookingService = bookingService;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    // Retrieve or create a booking ownership token
    private String getBookingOwnerToken(HttpSession session) {

        String token = (String) session.getAttribute(BOOKING_TOKEN);

        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(BOOKING_TOKEN, token);
        }

        return token;
    }

    // Start a booking
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody CreateBookingRequest request,
            HttpSession session) {

        try {
            String token = getBookingOwnerToken(session);

            Booking booking = bookingService.createBooking(
                    request.showtimeId(),
                    request.adultTickets(),
                    request.childTickets(),
                    request.seniorTickets(),
                    token
            );

            return ResponseEntity.ok(Map.of(
                    "bookingId", booking.getId(),
                    "showtimeId", booking.getShowtime().getId(),
                    "totalTickets", booking.getTotalTickets(),
                    "status", booking.getStatus().name()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Retrieve unavailable seats
    @GetMapping("/showtime/{showtimeId}/unavailable-seats")
    public ResponseEntity<?> getUnavailableSeats(
            @PathVariable Long showtimeId) {

        try {
            return ResponseEntity.ok(
                    bookingService.getUnavailableSeats(showtimeId)
            );

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Reserve selected seats
    @PostMapping("/{bookingId}/seats")
    public ResponseEntity<?> reserveSeats(
            @PathVariable Long bookingId,
            @RequestBody ReserveSeatsRequest request,
            HttpSession session) {

        try {
            String token = (String) session.getAttribute(BOOKING_TOKEN);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error",
                                "No active booking session found."
                        ));
            }

            Booking booking = bookingService.reserveSeats(
                    bookingId,
                    request.selectedSeats(),
                    token
            );

            List<String> seats = bookingSeatRepository
                    .findByBookingId(bookingId)
                    .stream()
                    .map(BookingSeat::getSeatNumber)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "bookingId", booking.getId(),
                    "selectedSeats", seats,
                    "status", booking.getStatus().name()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Attach booking to authenticated user
    @PostMapping("/{bookingId}/attach-user")
    public ResponseEntity<?> attachBookingToUser(
            @PathVariable Long bookingId,
            HttpSession session) {

        Object userIdObject = session.getAttribute("userId");

        if (!(userIdObject instanceof Long userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "Please log in to continue checkout."
                    ));
        }

        String token = (String) session.getAttribute(BOOKING_TOKEN);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "No active booking session found."
                    ));
        }

        try {
            Booking booking = bookingService.attachBookingToUser(
                    bookingId,
                    userId,
                    token
            );

            return ResponseEntity.ok(Map.of(
                    "bookingId", booking.getId(),
                    "userId", booking.getUser().getUserId(),
                    "status", booking.getStatus().name(),
                    "message", "Booking linked to user successfully."
            ));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Retrieve Order Summary
    @GetMapping("/{bookingId}/summary")
    public ResponseEntity<?> getBookingSummary(
            @PathVariable Long bookingId,
            HttpSession session) {

        Object userIdObject = session.getAttribute("userId");

        if (!(userIdObject instanceof Long userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "Please log in to continue checkout."
                    ));
        }

        String token = (String) session.getAttribute(BOOKING_TOKEN);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "No active booking session found."
                    ));
        }

        String emailAddress =
                (String) session.getAttribute("emailAddress");

        if (emailAddress == null || emailAddress.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "Unable to identify the logged-in user."
                    ));
        }

        try {
            BookingSummaryResponse summary =
                    bookingService.getBookingSummary(
                            bookingId,
                            userId,
                            token,
                            emailAddress
                    );

            return ResponseEntity.ok(summary);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Confirm or update checkout email
    @PutMapping("/{bookingId}/checkout-email")
    public ResponseEntity<?> updateCheckoutEmail(
            @PathVariable Long bookingId,
            @RequestBody CheckoutEmailRequest request,
            HttpSession session) {

        Object userIdObject = session.getAttribute("userId");

        if (!(userIdObject instanceof Long userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "Please log in to continue checkout."
                    ));
        }

        String token = (String) session.getAttribute(BOOKING_TOKEN);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error",
                            "No active booking session found."
                    ));
        }

        try {
            Booking booking = bookingService.updateCheckoutEmail(
                    bookingId,
                    userId,
                    token,
                    request.emailAddress()
            );

            return ResponseEntity.ok(Map.of(
                    "bookingId", booking.getId(),
                    "checkoutEmail", booking.getCheckoutEmail(),
                    "status", booking.getStatus().name(),
                    "message", "Checkout email saved successfully."
            ));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Request DTOs
    public record CreateBookingRequest(
            Long showtimeId,
            int adultTickets,
            int childTickets,
            int seniorTickets
    ) {}

    public record ReserveSeatsRequest(
            List<String> selectedSeats
    ) {}

    public record CheckoutEmailRequest(
            String emailAddress
    ) {}
}
