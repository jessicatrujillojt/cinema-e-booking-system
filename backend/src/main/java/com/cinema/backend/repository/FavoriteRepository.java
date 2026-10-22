package com.cinema.backend.repository;

import com.cinema.backend.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserUserId(Long userId);

    Optional<Favorite> findByUserUserIdAndMovieId(
            Long userId,
            Long movieId
    );

    boolean existsByUserUserIdAndMovieId(
            Long userId,
            Long movieId
    );
}