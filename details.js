const API_BASE = "http://localhost:8080";

const params = new URLSearchParams(window.location.search);
const movieId = params.get("id");


async function loadMovieDetails() {

    try {

        // Load movie information
        const movieResponse = await fetch(
            `${API_BASE}/api/movies/${movieId}`
        );

        if (!movieResponse.ok) {
            throw new Error("Failed to load movie.");
        }

        const movie = await movieResponse.json();

        document.getElementById("movie-title").textContent =
            movie.title;

        document.getElementById("movie-description").textContent =
            movie.description;

        document.getElementById("movie-rating").textContent =
            movie.rating;

        document.getElementById("movie-poster").src =
            movie.posterUrl;

        document.getElementById("movie-poster").alt =
            movie.title;

        document.getElementById("movie-trailer").src =
            movie.trailerUrl;


        // Load real showtimes from the database
        const showtimeResponse = await fetch(
            `${API_BASE}/api/showtimes/movie/${movieId}`
        );

        if (!showtimeResponse.ok) {
            throw new Error("Failed to load showtimes.");
        }

        const showtimes =
            await showtimeResponse.json();

        const showtimeContainer =
            document.getElementById("showtime-buttons");

        showtimeContainer.innerHTML = "";


        // If there are no showtimes
        if (showtimes.length === 0) {

            const message =
                document.createElement("p");

            message.textContent =
                "No showtimes are currently available.";

            showtimeContainer.appendChild(message);

            return;
        }


        // Create a button for each database showtime
        showtimes.forEach(function (showtime) {

            const button =
                document.createElement("button");

            const formattedTime =
                formatTime(showtime.showTime);

            button.textContent =
                `${showtime.showDate} - ${formattedTime}`;

            button.classList.add("time");


            button.addEventListener(
                "click",
                function () {

                    window.location.href =
                        `booking page.html?showtimeId=${showtime.id}` +
                        `&movie=${encodeURIComponent(movie.title)}` +
                        `&date=${encodeURIComponent(showtime.showDate)}` +
                        `&time=${encodeURIComponent(formattedTime)}` +
                        `&poster=${encodeURIComponent(movie.posterUrl)}` +
                        `&rows=${showtime.showroom.rows}` +
                        `&seatsPerRow=${showtime.showroom.seatsPerRow}` +
                        `&showroom=${encodeURIComponent(showtime.showroom.name)}`;
                }
            );

            showtimeContainer.appendChild(button);
        });

    } catch (error) {

        console.error(
            "Error loading movie details:",
            error
        );
    }
}


function formatTime(time) {

    const parts = time.split(":");

    let hour = Number(parts[0]);
    const minute = parts[1];

    const period =
        hour >= 12 ? "PM" : "AM";

    hour = hour % 12;

    if (hour === 0) {
        hour = 12;
    }

    return `${hour}:${minute} ${period}`;
}


loadMovieDetails();