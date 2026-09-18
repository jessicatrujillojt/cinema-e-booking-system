package com.cinema.backend.model;
import jakarta.persistence.*;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private String rating;

    @Column(length = 2000)
    private String description;

    private String posterUrl;
    private String trailerUrl;
    private String status;

    public Movie() {
    }
}
