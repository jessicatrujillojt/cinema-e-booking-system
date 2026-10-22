package com.cinema.backend;

import com.cinema.backend.model.Favorite;
import com.cinema.backend.model.Movie;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.FavoriteRepository;
import com.cinema.backend.repository.MovieRepository;
import com.cinema.backend.repository.UserRepository;
import com.cinema.backend.service.FavoriteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(FavoriteService.class)
class FavoriteIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private FavoriteService favoriteService;

    private User testUser;
    private Movie testMovie;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("Customer");
        testUser.setEmailAddress("favorite@example.com");
        testUser.setPassword("test-password");

        testUser = userRepository.saveAndFlush(testUser);

        testMovie = movieRepository.findById(1L)
            .orElseThrow(() ->
                new IllegalStateException(
                        "Seeded test movie not found"));
    }

    @Test
    void shouldSaveFavoriteInH2() {

        favoriteService.addFavorite(
                testUser.getUserId(),
                testMovie.getId());

        favoriteRepository.flush();

        List<Favorite> favorites =
                favoriteRepository.findByUserUserId(
                        testUser.getUserId());

        assertEquals(1, favorites.size());

        assertEquals(
                testMovie.getId(),
                favorites.get(0).getMovie().getId());
    }

    @Test
    void shouldPreventDuplicateFavorites() {

        favoriteService.addFavorite(
                testUser.getUserId(),
                testMovie.getId());

        favoriteService.addFavorite(
                testUser.getUserId(),
                testMovie.getId());

        favoriteRepository.flush();

        assertEquals(
                1,
                favoriteRepository
                        .findByUserUserId(
                                testUser.getUserId())
                        .size());
    }

    @Test
    void shouldRetrieveSavedFavorites() {

        favoriteService.addFavorite(
                testUser.getUserId(),
                testMovie.getId());

        List<Movie> movies =
                favoriteService.getFavorites(
                        testUser.getUserId());

        assertEquals(1, movies.size());
        assertEquals(
                testMovie.getId(),
                movies.get(0).getId());
    }

    @Test
    void shouldRemoveFavoriteFromH2() {

        favoriteService.addFavorite(
                testUser.getUserId(),
                testMovie.getId());

        favoriteService.removeFavorite(
                testUser.getUserId(),
                testMovie.getId());

        favoriteRepository.flush();

        assertTrue(
                favoriteRepository
                        .findByUserUserId(
                                testUser.getUserId())
                        .isEmpty());
    }
}