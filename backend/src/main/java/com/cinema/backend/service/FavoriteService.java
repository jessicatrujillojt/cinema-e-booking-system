package com.cinema.backend.service;

import com.cinema.backend.model.Favorite;
import com.cinema.backend.model.Movie;
import com.cinema.backend.model.User;

import com.cinema.backend.repository.FavoriteRepository;
import com.cinema.backend.repository.MovieRepository;
import com.cinema.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            MovieRepository movieRepository,
            UserRepository userRepository) {

        this.favoriteRepository = favoriteRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    // Get the user's favorite movies
    @Transactional(readOnly = true)
    public List<Movie> getFavorites(Long userId) {

        return favoriteRepository.findByUserUserId(userId)
                .stream()
                .map(Favorite::getMovie)
                .toList();
    }

    // Add a movie to favorites
    @Transactional
    public void addFavorite(Long userId, Long movieId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new NoSuchElementException("Movie not found"));

        // Prevent duplicate favorites
        if (favoriteRepository.existsByUserUserIdAndMovieId(
                userId, movieId)) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setMovie(movie);

        favoriteRepository.save(favorite);
    }

    // Remove a movie from favorites
    @Transactional
    public void removeFavorite(Long userId, Long movieId) {

        Favorite favorite = favoriteRepository
                .findByUserUserIdAndMovieId(userId, movieId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Movie is not in favorites"));

        favoriteRepository.delete(favorite);
    }
}