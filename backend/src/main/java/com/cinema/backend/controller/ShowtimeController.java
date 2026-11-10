package com.cinema.backend.controller;

import com.cinema.backend.model.Movie;
import com.cinema.backend.model.Showroom;
import com.cinema.backend.model.Showtime;
import com.cinema.backend.repository.MovieRepository;
import com.cinema.backend.repository.ShowroomRepository;
import com.cinema.backend.repository.ShowtimeRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
@CrossOrigin(origins = "*")
public class ShowtimeController {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final ShowroomRepository showroomRepository;

    public ShowtimeController(
            ShowtimeRepository showtimeRepository,
            MovieRepository movieRepository,
            ShowroomRepository showroomRepository
    ) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
        this.showroomRepository = showroomRepository;
    }


    // Schedule a new showtime
    @PostMapping
    public ResponseEntity<?> addShowtime(
            @RequestBody ShowtimeRequest request
    ) {

        if (
                request.getMovieId() == null ||
                request.getShowroomId() == null ||
                request.getShowDate() == null ||
                request.getShowTime() == null
        ) {
            return ResponseEntity
                    .badRequest()
                    .body("Movie, showroom, date, and time are required.");
        }


        Movie movie = movieRepository
                .findById(request.getMovieId())
                .orElse(null);

        if (movie == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Movie not found.");
        }


        Showroom showroom = showroomRepository
                .findById(request.getShowroomId())
                .orElse(null);

        if (showroom == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Showroom not found.");
        }


        boolean conflict =
                showtimeRepository
                        .existsByShowroomIdAndShowDateAndShowTime(
                                request.getShowroomId(),
                                request.getShowDate(),
                                request.getShowTime()
                        );


        if (conflict) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            "This showroom already has a movie scheduled at that date and time."
                    );
        }


        Showtime showtime = new Showtime();

        showtime.setMovie(movie);
        showtime.setShowroom(showroom);
        showtime.setShowDate(request.getShowDate());
        showtime.setShowTime(request.getShowTime());


        Showtime savedShowtime =
                showtimeRepository.save(showtime);


        return ResponseEntity.ok(savedShowtime);
    }


    // Get all showtimes for one movie
    @GetMapping("/movie/{movieId}")
    public List<Showtime> getShowtimesByMovie(
            @PathVariable Long movieId
    ) {
        return showtimeRepository.findByMovieId(movieId);
    }


    // Request body used when scheduling a showtime
    public static class ShowtimeRequest {

        private Long movieId;
        private Long showroomId;
        private LocalDate showDate;
        private LocalTime showTime;

        public ShowtimeRequest() {
        }

        public Long getMovieId() {
            return movieId;
        }

        public void setMovieId(Long movieId) {
            this.movieId = movieId;
        }

        public Long getShowroomId() {
            return showroomId;
        }

        public void setShowroomId(Long showroomId) {
            this.showroomId = showroomId;
        }

        public LocalDate getShowDate() {
            return showDate;
        }

        public void setShowDate(LocalDate showDate) {
            this.showDate = showDate;
        }

        public LocalTime getShowTime() {
            return showTime;
        }

        public void setShowTime(LocalTime showTime) {
            this.showTime = showTime;
        }
    }
}