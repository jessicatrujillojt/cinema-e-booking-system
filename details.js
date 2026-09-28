const params = new URLSearchParams(window.location.search);
const movieId = params.get("id");

fetch(`http://localhost:8080/api/movies/${movieId}`)
    .then(response => response.json())
    .then(movie => {
        document.getElementById("movie-title").textContent = movie.title;
        document.getElementById("movie-description").textContent = movie.description;
        document.getElementById("movie-rating").textContent = movie.rating;
        document.getElementById("movie-trailer").src = movie.trailerUrl;

        const showtimeContainer =
            document.getElementById("showtime-buttons");

        movie.showtimes.forEach(function(showtime) {
            const button = document.createElement("button");

            button.textContent = showtime;
            button.classList.add("time");

            showtimeContainer.appendChild(button);
        });
    })
    .catch(error => {
        console.error("Error loading movie details:", error);
    });