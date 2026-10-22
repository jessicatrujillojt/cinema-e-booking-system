package com.cinema.backend.controller;

import com.cinema.backend.model.Movie;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.UserRepository;
import com.cinema.backend.service.FavoriteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    public FavoriteController(
            FavoriteService favoriteService,
            UserRepository userRepository) {

        this.favoriteService = favoriteService;
        this.userRepository = userRepository;
    }

    // View the logged-in user's favorite movies
    @GetMapping
    public ResponseEntity<List<Movie>> getFavorites(
            Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                favoriteService.getFavorites(userId));
    }

    // Add a movie while browsing
    @PostMapping("/{movieId}")
    public ResponseEntity<Void> addFavorite(
            Authentication authentication,
            @PathVariable Long movieId) {

        Long userId = getAuthenticatedUserId(authentication);

        favoriteService.addFavorite(userId, movieId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    // Remove a movie from favorites
    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> removeFavorite(
            Authentication authentication,
            @PathVariable Long movieId) {

        Long userId = getAuthenticatedUserId(authentication);

        favoriteService.removeFavorite(userId, movieId);

        return ResponseEntity.noContent().build();
    }

    // Identify the authenticated user
    private Long getAuthenticatedUserId(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login required");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "User account not found"));

        return user.getId();
    }
}