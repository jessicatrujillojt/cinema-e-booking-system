package com.cinema.backend.repository;

import com.cinema.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAddress(String emailAddress);
    boolean existsByEmailAddress(String emailAddress);
}