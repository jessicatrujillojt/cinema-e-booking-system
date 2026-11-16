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
            `${API_BASE}/api/movies/admin/all`,
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

        movies
            .filter(movie => movie.visible)
            .forEach(function (movie) {

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

            image.src = movie.posterUrl;
            image.alt = movie.title;
            image.classList.add("imgtable");

            imageCell.appendChild(image);


            const titleCell =
                document.createElement("td");

            titleCell.textContent = movie.title;


            const genreCell =
                document.createElement("td");

            genreCell.textContent = movie.genre;


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
                movie.visible
                    ? "Visible"
                    : "Hidden";


            const actionsCell =
                document.createElement("td");


            const editButton =
                document.createElement("button");

            editButton.textContent =
                "Edit";

            editButton.addEventListener(
                "click",
                function () {
                    editMovie(movie);
                }
            );


            const visibilityButton =
                document.createElement("button");

            visibilityButton.textContent =
                movie.visible
                    ? "Hide"
                    : "Show";

            visibilityButton.addEventListener(
                "click",
                function () {
                    toggleMovieVisibility(
                        movie.id
                    );
                }
            );


            const deleteButton =
                document.createElement("button");

            deleteButton.textContent =
                "Delete";

            deleteButton.addEventListener(
                "click",
                function () {
                    deleteMovie(
                        movie.id,
                        movie.title
                    );
                }
            );


            actionsCell.append(
                editButton,
                visibilityButton,
                deleteButton
            );


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

async function toggleMovieVisibility(movieId) {

    try {

        const response = await fetch(
            `${API_BASE}/api/movies/${movieId}/visibility`,
            {
                method: "PATCH",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error(
                "Unable to update movie visibility."
            );
        }

        await response.json();

        await loadMovies();

    } catch (error) {

        console.error(
            "Visibility error:",
            error
        );

        alert(
            "Unable to update movie visibility."
        );
    }
}


async function deleteMovie(
    movieId,
    movieTitle
) {

    const confirmed =
        confirm(
            `Delete "${movieTitle}"?`
        );

    if (!confirmed) {
        return;
    }

    try {

        const response = await fetch(
            `${API_BASE}/api/movies/${movieId}`,
            {
                method: "DELETE",
                credentials: "include"
            }
        );

        const result =
            await response.json();

        if (!response.ok) {

            alert(
                result.error ||
                "Unable to delete movie."
            );

            return;
        }

        alert(
            result.message
        );

        await loadMovies();

    } catch (error) {

        console.error(
            "Delete movie error:",
            error
        );

        alert(
            "Unable to delete movie."
        );
    }
}


async function editMovie(movie) {

    const newTitle =
        prompt(
            "Movie title:",
            movie.title
        );

    if (newTitle === null) {
        return;
    }

    const newGenre =
        prompt(
            "Genre:",
            movie.genre
        );

    if (newGenre === null) {
        return;
    }

    const newRating =
        prompt(
            "Rating:",
            movie.rating
        );

    if (newRating === null) {
        return;
    }

    const updatedMovie = {
        title: newTitle.trim(),
        genre: newGenre.trim(),
        rating: newRating.trim(),
        description: movie.description,
        posterUrl: movie.posterUrl,
        trailerUrl: movie.trailerUrl,
        status: movie.status
    };

    try {

        const response = await fetch(
            `${API_BASE}/api/movies/${movie.id}`,
            {
                method: "PUT",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                credentials: "include",

                body:
                    JSON.stringify(
                        updatedMovie
                    )
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
            "Movie updated successfully."
        );

        await loadMovies();

    } catch (error) {

        console.error(
            "Edit movie error:",
            error
        );

        alert(
            "Unable to edit movie."
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