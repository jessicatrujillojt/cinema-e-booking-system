package com.cinema.backend.controller;

import com.cinema.backend.model.Showroom;
import com.cinema.backend.repository.ShowroomRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showrooms")
@CrossOrigin(origins = "*")
public class ShowroomController {

    private final ShowroomRepository showroomRepository;

    public ShowroomController(ShowroomRepository showroomRepository) {
        this.showroomRepository = showroomRepository;
    }

    // Get all showrooms
    @GetMapping
    public List<Showroom> getAllShowrooms() {
        return showroomRepository.findAll();
    }
}