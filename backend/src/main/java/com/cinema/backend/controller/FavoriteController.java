package com.cinema.backend.controller;

import com.cinema.backend.model.Movie;
import com.cinema.backend.service.FavoriteService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(
            FavoriteService favoriteService) {

        this.favoriteService = favoriteService;
    }

    // View the logged-in user's favorite movies
    @GetMapping
    public ResponseEntity<List<Movie>> getFavorites(
            HttpSession session) {

        Long userId = getAuthenticatedUserId(session);

        return ResponseEntity.ok(
                favoriteService.getFavorites(userId));
    }

    // Add a movie while browsing
    @PostMapping("/{movieId}")
    public ResponseEntity<Void> addFavorite(
            HttpSession session,
            @PathVariable Long movieId) {

        Long userId = getAuthenticatedUserId(session);

        favoriteService.addFavorite(userId, movieId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    // Remove a movie from favorites
    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> removeFavorite(
            HttpSession session,
            @PathVariable Long movieId) {

        Long userId = getAuthenticatedUserId(session);

        favoriteService.removeFavorite(userId, movieId);

        return ResponseEntity.noContent().build();
    }

    // Identify the logged-in user from the session
    private Long getAuthenticatedUserId(
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login required");
        }

        return (Long) userId;
    }
}