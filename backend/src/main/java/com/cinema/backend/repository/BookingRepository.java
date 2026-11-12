package com.cinema.backend.repository;

import com.cinema.backend.model.Booking;
import com.cinema.backend.model.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    // Find bookings for a specific showtime
    List<Booking> findByShowtimeId(Long showtimeId);

    // Find bookings belonging to a specific user
    List<Booking> findByUser_UserId(Long userId);

    // Find bookings with specific statuses
    List<Booking> findByShowtimeIdAndStatusIn(
            Long showtimeId,
            List<BookingStatus> statuses
    );
}
