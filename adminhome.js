const API_BASE = "http://localhost:8080";


async function checkAdminAccess() {

    try {

        const response = await fetch(
            `${API_BASE}/api/admin/check`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {

            window.location.href =
                "login.html";
        }

    } catch (error) {

        console.error(
            "Admin access check error:",
            error
        );

        window.location.href =
            "login.html";
    }
}


async function logout() {

    try {

        await fetch(
            `${API_BASE}/api/auth/logout`,
            {
                method: "POST",
                credentials: "include"
            }
        );

        window.location.href =
            "login.html";

    } catch (error) {

        console.error(
            "Logout error:",
            error
        );
    }
}

async function addMovie(event) {
    event.preventDefault();

    const movie = {
        title: document.getElementById("Title").value,
        description: document.getElementById("Description").value,
        posterUrl: document.getElementById("PosterURL").value,
        trailerUrl: document.getElementById("TrailerURL").value,
        rating: document.getElementById("rating").value,
        genre: document.getElementById("Genre").value,
        status: document.getElementById("status").value
    };

    try {
        const response = await fetch(`${API_BASE}/api/movies`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(movie)
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            alert(errorMessage);
            return;
        }

        const savedMovie = await response.json();

        alert(`Movie "${savedMovie.title}" added successfully.`);

        await loadMovies();

        document
        .getElementById("cardForm")
        .reset();
    } catch (error) {
        console.error("Add movie error:", error);
        alert("Unable to add movie.");
    }
}

async function loadMovies() {
    try {
        const response = await fetch(
            `${API_BASE}/api/movies`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error("Failed to load movies.");
        }

        const movies = await response.json();


        // Populate Schedule Showtime movie dropdown
        const movieSelect =
            document.getElementById("showtimeMovie");

        movieSelect.innerHTML =
            '<option value="">Select a movie</option>';

        movies.forEach(function (movie) {
            const option =
                document.createElement("option");

            option.value = movie.id;
            option.textContent = movie.title;

            movieSelect.appendChild(option);
        });


        // Populate admin movie table
        const movieTableBody =
            document.getElementById("movieTableBody");

        const headerRow =
            movieTableBody.querySelector("tr");

        movieTableBody.innerHTML = "";

        movieTableBody.appendChild(headerRow);


        movies.forEach(function (movie) {

            const row =
                document.createElement("tr");


            const imageCell =
                document.createElement("td");

            const image =
                document.createElement("img");

            image.src =
                movie.posterUrl;

            image.alt =
                movie.title;

            image.classList.add("imgtable");

            imageCell.appendChild(image);


            const titleCell =
                document.createElement("td");

            titleCell.textContent =
                movie.title;


            const genreCell =
                document.createElement("td");

            genreCell.textContent =
                movie.genre;


            const showroomCell =
                document.createElement("td");

            showroomCell.textContent =
                "Scheduled separately";


            const ratingCell =
                document.createElement("td");

            ratingCell.textContent =
                movie.rating;


            const visibilityCell =
                document.createElement("td");

            visibilityCell.textContent =
                movie.status;


            const actionsCell =
                document.createElement("td");

            actionsCell.textContent =
                "—";


            row.append(
                imageCell,
                titleCell,
                genreCell,
                showroomCell,
                ratingCell,
                visibilityCell,
                actionsCell
            );


            movieTableBody.appendChild(row);
        });

    } catch (error) {

        console.error(
            "Load movies error:",
            error
        );
    }
}


async function loadShowrooms() {
    try {
        const response = await fetch(
            `${API_BASE}/api/showrooms`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error("Failed to load showrooms.");
        }

        const showrooms = await response.json();

        const showroomSelect =
            document.getElementById("showroom");

        showroomSelect.innerHTML =
            '<option value="">Select a showroom</option>';

        showrooms.forEach(function (showroom) {
            const option =
                document.createElement("option");

            option.value = showroom.id;
            option.textContent = showroom.name;

            showroomSelect.appendChild(option);
        });

    } catch (error) {
        console.error(
            "Load showrooms error:",
            error
        );
    }
}


async function addShowtime(event) {
    event.preventDefault();

    const showtime = {
        movieId: Number(
            document.getElementById(
                "showtimeMovie"
            ).value
        ),

        showroomId: Number(
            document.getElementById(
                "showroom"
            ).value
        ),

        showDate:
            document.getElementById(
                "event-date"
            ).value,

        showTime:
            document.getElementById(
                "showtime"
            ).value
    };

    try {
        const response = await fetch(
            `${API_BASE}/api/showtimes`,
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                credentials: "include",

                body: JSON.stringify(showtime)
            }
        );

        if (!response.ok) {
            const errorMessage =
                await response.text();

            alert(errorMessage);
            return;
        }

        await response.json();

        alert(
            "Showtime added successfully."
        );

        document
            .getElementById("showtimeForm")
            .reset();

    } catch (error) {
        console.error(
            "Add showtime error:",
            error
        );

        alert(
            "Unable to add showtime."
        );
    }
}

document.addEventListener(
    "DOMContentLoaded",
    function () {

        checkAdminAccess();

        loadMovies();
        loadShowrooms();

        document
            .getElementById("logout-button")
            .addEventListener(
                "click",
                logout
            );

        document
            .getElementById("cardForm")
            .addEventListener(
                "submit",
                addMovie
            );

        document
            .getElementById("showtimeForm")
            .addEventListener(
                "submit",
                addShowtime
            );

        document
            .getElementById("addCardButton")
            .addEventListener(
                "click",
                function () {
                    document
                        .getElementById("cardFormSection")
                        .scrollIntoView({
                            behavior: "smooth"
                        });
            }
        );

        document
            .getElementById("cancelCardButton")
            .addEventListener(
                "click",
                function () {
                    document
                        .getElementById("cardForm")
                        .reset();
            }
        );
    }

    
);