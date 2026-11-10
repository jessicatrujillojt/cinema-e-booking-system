package com.cinema.backend.repository;

import com.cinema.backend.model.Showroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowroomRepository extends JpaRepository<Showroom, Long> {
}