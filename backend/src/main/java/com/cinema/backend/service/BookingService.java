
package com.cinema.backend.service;

import com.cinema.backend.dto.BookingSummaryResponse;
import com.cinema.backend.model.*;
import com.cinema.backend.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;
    private final TicketPricingService ticketPricingService;

    public BookingService(
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            ShowtimeRepository showtimeRepository,
            UserRepository userRepository,
            TicketPricingService ticketPricingService) {

        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.showtimeRepository = showtimeRepository;
        this.userRepository = userRepository;
        this.ticketPricingService = ticketPricingService;
    }

    // Create a booking with ticket quantities
    @Transactional
    public Booking createBooking(
            Long showtimeId,
            int adultTickets,
            int childTickets,
            int seniorTickets,
            String bookingOwnerToken) {

        if (bookingOwnerToken == null ||
                bookingOwnerToken.isBlank()) {
            throw new IllegalArgumentException(
                    "A booking session is required.");
        }

        if (showtimeId == null) {
            throw new IllegalArgumentException(
                    "Showtime is required.");
        }

        if (adultTickets < 0 ||
                childTickets < 0 ||
                seniorTickets < 0) {
            throw new IllegalArgumentException(
                    "Ticket quantities cannot be negative.");
        }

        long total = (long) adultTickets
                + childTickets + seniorTickets;

        if (total <= 0) {
            throw new IllegalArgumentException(
                    "Select at least one ticket.");
        }

        Showtime showtime = showtimeRepository
                .findById(showtimeId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Showtime not found."));

        Showroom showroom = showtime.getShowroom();

        if (showroom == null ||
                showroom.getRows() < 1 ||
                showroom.getRows() > 26 ||
                showroom.getSeatsPerRow() < 1) {
            throw new IllegalArgumentException(
                    "Invalid showroom seating configuration.");
        }

        long capacity = (long) showroom.getRows()
                * showroom.getSeatsPerRow();

        if (total > capacity) {
            throw new IllegalArgumentException(
                    "Ticket quantity exceeds showroom capacity.");
        }

        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setAdultTickets(adultTickets);
        booking.setChildTickets(childTickets);
        booking.setSeniorTickets(seniorTickets);
        booking.setBookingOwnerToken(bookingOwnerToken);
        booking.setStatus(BookingStatus.IN_PROGRESS);

        return bookingRepository.save(booking);
    }

    // Retrieve unavailable seats for a showtime
    @Transactional(readOnly = true)
    public List<String> getUnavailableSeats(Long showtimeId) {

        if (!showtimeRepository.existsById(showtimeId)) {
            throw new NoSuchElementException(
                    "Showtime not found.");
        }

        return bookingSeatRepository.findUnavailableSeatNumbers(
                showtimeId,
                List.of(
                        BookingStatus.RESERVED,
                        BookingStatus.CONFIRMED
                )
        );
    }

    // Validate and reserve selected seats
    @Transactional
    public Booking reserveSeats(
            Long bookingId,
            List<String> selectedSeats,
            String bookingOwnerToken) {

        if (bookingOwnerToken == null ||
                bookingOwnerToken.isBlank()) {
            throw new SecurityException(
                    "A valid booking session is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found."));

        // Verify session ownership
        if (!bookingOwnerToken.equals(
                booking.getBookingOwnerToken())) {
            throw new SecurityException(
                    "You cannot modify another session's booking.");
        }

        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Booking is not available for seat selection.");
        }

        if (selectedSeats == null ||
                selectedSeats.size() != booking.getTotalTickets()) {
            throw new IllegalArgumentException(
                    "Selected seats must match ticket quantity.");
        }

        Showtime showtime = booking.getShowtime();
        Showroom showroom = showtime.getShowroom();

        int rows = showroom.getRows();
        int seatsPerRow = showroom.getSeatsPerRow();

        if (rows < 1 || rows > 26 || seatsPerRow < 1) {
            throw new IllegalArgumentException(
                    "Invalid showroom seating configuration.");
        }

        Set<String> uniqueSeats = new HashSet<>();

        for (String seat : selectedSeats) {

            if (seat == null ||
                    !seat.matches("[A-Z][1-9][0-9]*")) {
                throw new IllegalArgumentException(
                        "Invalid seat number: " + seat);
            }

            int rowIndex = seat.charAt(0) - 'A';
            int seatIndex;

            try {
                seatIndex = Integer.parseInt(
                        seat.substring(1));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid seat number: " + seat);
            }

            if (rowIndex >= rows ||
                    seatIndex > seatsPerRow) {
                throw new IllegalArgumentException(
                        "Seat does not exist in this showroom: "
                                + seat);
            }

            if (!uniqueSeats.add(seat)) {
                throw new IllegalArgumentException(
                        "Duplicate seat selection.");
            }
        }

        List<String> unavailable = getUnavailableSeats(
                showtime.getId());

        for (String seat : selectedSeats) {
            if (unavailable.contains(seat)) {
                throw new IllegalArgumentException(
                        "Seat " + seat + " is already reserved.");
            }
        }

        // Save selected seats
        for (String seat : selectedSeats) {
            bookingSeatRepository.save(
                    new BookingSeat(booking, seat)
            );
        }

        booking.setStatus(BookingStatus.RESERVED);

        return bookingRepository.save(booking);
    }

    // Link a reserved booking to the logged-in user
    @Transactional
    public Booking attachBookingToUser(
            Long bookingId,
            Long userId,
            String bookingOwnerToken) {

        if (userId == null) {
            throw new SecurityException(
                    "You must be logged in to continue checkout.");
        }

        if (bookingOwnerToken == null ||
                bookingOwnerToken.isBlank()) {
            throw new SecurityException(
                    "A valid booking session is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found."));

        if (!bookingOwnerToken.equals(
                booking.getBookingOwnerToken())) {
            throw new SecurityException(
                    "You cannot access another session's booking.");
        }

        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalArgumentException(
                    "Seats must be reserved before checkout.");
        }

        if (booking.getUser() != null &&
                !booking.getUser().getUserId().equals(userId)) {
            throw new SecurityException(
                    "This booking belongs to another user.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "User not found."));

        booking.setUser(user);

        return bookingRepository.save(booking);
    }

    // Retrieve the order summary
    @Transactional(readOnly = true)
    public BookingSummaryResponse getBookingSummary(
            Long bookingId,
            Long userId,
            String bookingOwnerToken,
            String emailAddress) {

        if (userId == null) {
            throw new SecurityException(
                    "Please log in to continue checkout.");
        }

        if (bookingOwnerToken == null ||
                bookingOwnerToken.isBlank()) {
            throw new SecurityException(
                    "A valid booking session is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found."));

        // Verify session ownership
        if (!bookingOwnerToken.equals(
                booking.getBookingOwnerToken())) {
            throw new SecurityException(
                    "You cannot access another session's booking.");
        }

        // Verify authenticated user owns the booking
        if (booking.getUser() == null ||
                !userId.equals(booking.getUser().getUserId())) {
            throw new SecurityException(
                    "This booking does not belong to your account.");
        }

        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalArgumentException(
                    "Seats must be reserved before checkout.");
        }

        Showtime showtime = booking.getShowtime();

        List<String> seats = bookingSeatRepository
                .findByBookingId(bookingId)
                .stream()
                .map(BookingSeat::getSeatNumber)
                .sorted()
                .toList();

        BigDecimal total = ticketPricingService.calculateTotal(
                booking.getAdultTickets(),
                booking.getChildTickets(),
                booking.getSeniorTickets()
        );

        String checkoutEmail = booking.getCheckoutEmail();

        if (checkoutEmail == null || checkoutEmail.isBlank()) {
            checkoutEmail = emailAddress;
        }

        return new BookingSummaryResponse(
                booking.getId(),
                showtime.getMovie().getTitle(),
                showtime.getShowDate(),
                showtime.getShowTime(),
                showtime.getShowroom().getName(),
                seats,
                booking.getAdultTickets(),
                booking.getChildTickets(),
                booking.getSeniorTickets(),
                booking.getTotalTickets(),
                ticketPricingService.getAdultPrice(),
                ticketPricingService.getChildPrice(),
                ticketPricingService.getSeniorPrice(),
                total,
                checkoutEmail,
                booking.getStatus().name()
        );
    }

    // Confirm or update checkout email
    @Transactional
    public Booking updateCheckoutEmail(
            Long bookingId,
            Long userId,
            String bookingOwnerToken,
            String checkoutEmail) {

        if (userId == null) {
            throw new SecurityException(
                    "Please log in to continue checkout.");
        }

        if (bookingOwnerToken == null ||
                bookingOwnerToken.isBlank()) {
            throw new SecurityException(
                    "A valid booking session is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Booking not found."));

        // Verify session ownership
        if (!bookingOwnerToken.equals(
                booking.getBookingOwnerToken())) {
            throw new SecurityException(
                    "You cannot modify another session's booking.");
        }

        // Verify authenticated user owns booking
        if (booking.getUser() == null ||
                !userId.equals(booking.getUser().getUserId())) {
            throw new SecurityException(
                    "This booking does not belong to your account.");
        }

        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalArgumentException(
                    "Seats must be reserved before checkout.");
        }

        if (checkoutEmail == null || checkoutEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Checkout email is required.");
        }

        String normalizedEmail = checkoutEmail.trim();

        if (normalizedEmail.length() > 255 ||
                !normalizedEmail.matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Please enter a valid email address.");
        }

        booking.setCheckoutEmail(normalizedEmail);

        return bookingRepository.save(booking);
    }
}
