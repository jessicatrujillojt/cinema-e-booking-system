package com.cinema.backend.controller;

import com.cinema.backend.model.Movie;
import com.cinema.backend.repository.MovieRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    // PUBLIC: Get only visible movies
    @GetMapping
    public List<Movie> getVisibleMovies() {
        return movieRepository.findByVisibleTrue();
    }


    // ADMIN: Get all movies, including hidden movies
    @GetMapping("/admin/all")
    public List<Movie> getAllMoviesForAdmin() {
        return movieRepository.findAll();
    }


    // Add a new movie
    @PostMapping
    public ResponseEntity<?> addMovie(
            @RequestBody Movie movie
    ) {

        if (
            movie.getTitle() == null ||
            movie.getTitle().trim().isEmpty()
        ) {
            return ResponseEntity
                    .badRequest()
                    .body("Movie title is required.");
        }

        if (
            movie.getGenre() == null ||
            movie.getGenre().trim().isEmpty()
        ) {
            return ResponseEntity
                    .badRequest()
                    .body("Movie genre is required.");
        }

        if (
            movie.getRating() == null ||
            movie.getRating().trim().isEmpty()
        ) {
            return ResponseEntity
                    .badRequest()
                    .body("Movie rating is required.");
        }

        movie.setVisible(true);

        Movie savedMovie =
                movieRepository.save(movie);

        return ResponseEntity.ok(savedMovie);
    }


    // Get one movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(
            @PathVariable Long id
    ) {

        Movie movie =
                movieRepository
                        .findById(id)
                        .orElse(null);

        if (movie == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(movie);
    }


    // Edit a movie
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(
            @PathVariable Long id,
            @RequestBody Movie updatedMovie
    ) {

        Movie movie =
                movieRepository
                        .findById(id)
                        .orElse(null);

        if (movie == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }


        if (
            updatedMovie.getTitle() == null ||
            updatedMovie.getTitle()
                    .trim()
                    .isEmpty()
        ) {
            return ResponseEntity
                    .badRequest()
                    .body(
                        "Movie title is required."
                    );
        }


        if (
            updatedMovie.getGenre() == null ||
            updatedMovie.getGenre()
                    .trim()
                    .isEmpty()
        ) {
            return ResponseEntity
                    .badRequest()
                    .body(
                        "Movie genre is required."
                    );
        }


        if (
            updatedMovie.getRating() == null ||
            updatedMovie.getRating()
                    .trim()
                    .isEmpty()
        ) {
            return ResponseEntity
                    .badRequest()
                    .body(
                        "Movie rating is required."
                    );
        }


        movie.setTitle(
                updatedMovie.getTitle()
        );

        movie.setGenre(
                updatedMovie.getGenre()
        );

        movie.setRating(
                updatedMovie.getRating()
        );

        movie.setDescription(
                updatedMovie.getDescription()
        );

        movie.setPosterUrl(
                updatedMovie.getPosterUrl()
        );

        movie.setTrailerUrl(
                updatedMovie.getTrailerUrl()
        );

        movie.setStatus(
                updatedMovie.getStatus()
        );


        Movie savedMovie =
                movieRepository.save(movie);

        return ResponseEntity.ok(savedMovie);
    }


    // Toggle visible / hidden
    @PatchMapping("/{id}/visibility")
    public ResponseEntity<?> toggleVisibility(
            @PathVariable Long id
    ) {

        Movie movie =
                movieRepository
                        .findById(id)
                        .orElse(null);

        if (movie == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        movie.setVisible(
                !movie.isVisible()
        );

        Movie savedMovie =
                movieRepository.save(movie);

        return ResponseEntity.ok(
                Map.of(
                    "id",
                    savedMovie.getId(),
                    "visible",
                    savedMovie.isVisible()
                )
        );
    }


    // Delete movie
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(
            @PathVariable Long id
    ) {

        if (!movieRepository.existsById(id)) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        try {

            movieRepository.deleteById(id);

            return ResponseEntity.ok(
                    Map.of(
                        "message",
                        "Movie deleted successfully."
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        Map.of(
                            "error",
                            "Movie cannot be deleted because it may be associated with existing showtimes or bookings."
                        )
                    );
        }
    }


    // Search visible movies by title
    @GetMapping("/search")
    public List<Movie> searchMovies(
            @RequestParam String title
    ) {

        return movieRepository
                .findByTitleContainingIgnoreCase(title)
                .stream()
                .filter(Movie::isVisible)
                .toList();
    }


    // Filter visible movies by genre
    @GetMapping("/genre")
    public List<Movie> filterByGenre(
            @RequestParam String genre
    ) {

        return movieRepository
                .findByGenreIgnoreCase(genre)
                .stream()
                .filter(Movie::isVisible)
                .toList();
    }
}