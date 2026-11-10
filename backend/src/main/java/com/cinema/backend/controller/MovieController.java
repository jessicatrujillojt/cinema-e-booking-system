package com.cinema.backend.controller;

import com.cinema.backend.model.Movie;
import com.cinema.backend.repository.MovieRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Get all movies
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Add a new movie
@PostMapping
    public ResponseEntity<?> addMovie(@RequestBody Movie movie) {

        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
         return ResponseEntity
                .badRequest()
                .body("Movie title is required.");
        }

        if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body("Movie genre is required.");
        }

        if (movie.getRating() == null || movie.getRating().trim().isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body("Movie rating is required.");
        }

    }

    Movie savedMovie =
            movieRepository.save(movie);

        return ResponseEntity.ok(savedMovie);
    }
    // Get one movie by ID
    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id).orElse(null);
    }   
    // Search movies by title
    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    // Filter movies by genre
    @GetMapping("/genre")
    public List<Movie> filterByGenre(@RequestParam String genre) {
        return movieRepository.findByGenreIgnoreCase(genre);
    }
}