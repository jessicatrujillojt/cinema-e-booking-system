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
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class FavoriteServiceTest {

    private FavoriteRepository favoriteRepository;
    private MovieRepository movieRepository;
    private UserRepository userRepository;
    private FavoriteService favoriteService;

    private User testUser;
    private Movie testMovie;

    @BeforeEach
    void setUp() {

        favoriteRepository = mock(FavoriteRepository.class);
        movieRepository = mock(MovieRepository.class);
        userRepository = mock(UserRepository.class);

        favoriteService = new FavoriteService(
                favoriteRepository,
                movieRepository,
                userRepository
        );

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("Customer");
        testUser.setEmailAddress("test@example.com");

        testMovie = mock(Movie.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        when(movieRepository.findById(1L))
                .thenReturn(Optional.of(testMovie));
    }

    @Test
    void shouldAddMovieToFavorites() {

        when(favoriteRepository.existsByUserUserIdAndMovieId(1L, 1L))
                .thenReturn(false);

        favoriteService.addFavorite(1L, 1L);

        ArgumentCaptor<Favorite> captor =
                ArgumentCaptor.forClass(Favorite.class);

        verify(favoriteRepository).save(captor.capture());

        Favorite savedFavorite = captor.getValue();

        assertEquals(testUser, savedFavorite.getUser());
        assertEquals(testMovie, savedFavorite.getMovie());
    }

    @Test
    void shouldPreventDuplicateFavorites() {

        when(favoriteRepository.existsByUserUserIdAndMovieId(1L, 1L))
                .thenReturn(true);

        favoriteService.addFavorite(1L, 1L);

        verify(favoriteRepository, never())
                .save(any(Favorite.class));
    }

    @Test
    void shouldRetrieveFavoriteMovies() {

        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setMovie(testMovie);

        when(favoriteRepository.findByUserUserId(1L))
                .thenReturn(List.of(favorite));

        List<Movie> favorites =
                favoriteService.getFavorites(1L);

        assertEquals(1, favorites.size());
        assertEquals(testMovie, favorites.get(0));
    }

    @Test
    void shouldRemoveMovieFromFavorites() {

        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setMovie(testMovie);

        when(favoriteRepository.findByUserUserIdAndMovieId(1L, 1L))
                .thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite(1L, 1L);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void shouldRejectAddingNonexistentMovie() {

        when(movieRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> favoriteService.addFavorite(1L, 99L)
        );

        verify(favoriteRepository, never())
                .save(any(Favorite.class));
    }
}