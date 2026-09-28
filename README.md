# Cinema E-Booking System

A web-based cinema e-booking system developed for CSCI 4050/6050 Software Engineering.

The project uses a Spring Boot backend, an H2 embedded database, and an HTML/CSS/JavaScript frontend.

# Sprint 1 Features

The current implementation includes:

- Dynamic movie listings loaded from the database
- Currently Running and Coming Soon movie categories
- Movie search by title
- Movie filtering by genre
- Movie details pages
- Movie posters, ratings, descriptions, and trailers
- Embedded YouTube trailer playback
- Movie showtimes
- Navigation from a selected showtime to the booking page
- Ticket quantity selection for adult, child, and senior tickets
- Seat selection prototype
- Automatic database seeding with 10 movies

Booking and checkout backend logic will be implemented in later sprints.

# Technology Stack

# Backend
- Java 21
- Spring Boot
- Spring Data JPA
- Maven

# Database
- H2 Embedded Database

# Frontend
- HTML
- CSS
- JavaScript

# Requirements

Before running the project, make sure you have:

- Java 21
- Git
- A web browser

VS Code is recommended for development.

# Running the Backend

The H2 database starts automatically with the Spring Boot application.

No MySQL installation or database configuration is required.

# Mac / Linux

From the project root:

```bash
./run.sh

# Windows
.\run.bat

