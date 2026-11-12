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

### Booking and Seating

POST /api/bookings

Creates a booking with the selected showtime and ticket quantities.

Example request:

```json
{
  "showtimeId": 1,
  "adultTickets": 2,
  "childTickets": 1,
  "seniorTickets": 0
}
```

Required fields:
- showtimeId
- adultTickets
- childTickets
- seniorTickets

Ticket quantities must be valid and cannot exceed showroom capacity.

### Seat Selection

GET /api/bookings/showtime/{showtimeId}/unavailable-seats

Returns seats that are already reserved or booked.

Example response:

```json
["A1", "A2", "A3"]
```

POST /api/bookings/{bookingId}/seats

Reserves selected seats for a booking.

Example request:

```json
{
  "selectedSeats": ["B1", "B2", "B3"]
}
```

The number of seats must match the number of tickets. Already reserved seats cannot be selected.

### Checkout

POST /api/bookings/{bookingId}/attach-user

Links the booking to the logged-in user. Login is required before checkout.

GET /api/bookings/{bookingId}/summary

Returns the movie, showtime, selected seats, ticket quantities, prices, total before tax, and checkout email.

Ticket prices:
- Adult: $12.00
- Child: $8.00
- Senior: $10.00

PUT /api/bookings/{bookingId}/checkout-email

Allows the user to confirm or change their checkout email.

Example request:

```json
{
  "emailAddress": "tickets@example.com"
}
```

### Booking Flow

Movie → Showtime → Tickets → Seat Selection → Login (if needed) → Order Summary → Email Confirmation → Payment Page

All session-based booking requests must use `credentials: "include"` to preserve the booking through login.

Payment processing and final booking confirmation are not required for Sprint 3.