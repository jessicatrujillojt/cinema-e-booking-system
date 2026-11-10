## Sprint 3 - Movies, Showrooms, and Showtimes

### Movies

GET /api/movies

Returns all movies.

GET /api/movies/{id}

Returns one movie by ID.

POST /api/movies

Creates a new movie.

Example request:

{
  "title": "Test Movie",
  "genre": "Drama",
  "rating": "PG-13",
  "description": "Movie description",
  "posterUrl": "https://example.com/poster.jpg",
  "trailerUrl": "https://example.com/trailer",
  "status": "Coming Soon"
}

Required backend fields:
- title
- genre
- rating


### Showrooms

GET /api/showrooms

Returns all available showrooms.

Example response:

[
  {
    "id": 1,
    "name": "Showroom 1",
    "rows": 5,
    "seatsPerRow": 8
  }
]


### Showtimes

POST /api/showtimes

Creates a new showtime.

Example request:

{
  "movieId": 1,
  "showroomId": 1,
  "showDate": "2026-11-20",
  "showTime": "19:00"
}

The backend rejects a request if the same showroom is already scheduled
for the same date and time.


GET /api/showtimes/movie/{movieId}

Returns all scheduled showtimes for one movie.

Example:

GET /api/showtimes/movie/1