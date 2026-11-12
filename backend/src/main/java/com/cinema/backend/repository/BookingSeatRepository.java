package com.cinema.backend.repository;

import com.cinema.backend.model.BookingSeat;
import com.cinema.backend.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BookingSeatRepository
        extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBookingId(Long bookingId);

    @Query("""
        SELECT bs.seatNumber
        FROM BookingSeat bs
        WHERE bs.booking.showtime.id = :showtimeId
        AND bs.booking.status IN :statuses
        """)
    List<String> findUnavailableSeatNumbers(
            @Param("showtimeId") Long showtimeId,
            @Param("statuses") Collection<BookingStatus> statuses
    );
}
