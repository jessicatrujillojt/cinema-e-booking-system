package com.cinema.backend.repository;

import com.cinema.backend.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    List<Showtime> findByMovieId(Long movieId);

    boolean existsByShowroomIdAndShowDateAndShowTime(
            Long showroomId,
            LocalDate showDate,
            LocalTime showTime
    );
}