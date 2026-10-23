const API_BASE = "http://localhost:8080";

const searchInput =
    document.getElementById("movie-search");

const genreSelect =
    document.getElementById("genre");

const movieGrid =
    document.getElementById("movie-grid");

const noResults =
    document.getElementById("no-results");

const loginButton =
    document.getElementById("login-button");

const logoutButton =
    document.getElementById("logout-button");


let favoriteMovieIds = new Set();
let loggedIn = false;


async function checkSession() {

    try {

        const response = await fetch(
            `${API_BASE}/api/auth/session`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        const data = await response.json();

        loggedIn = data.loggedIn === true;

        if (loggedIn) {

            loginButton.style.display = "none";
            logoutButton.style.display = "inline-block";

            await loadFavorites();

        } else {

            loginButton.style.display = "inline-block";
            logoutButton.style.display = "none";

            favoriteMovieIds.clear();
        }

    } catch (error) {

        console.error(
            "Session check error:",
            error
        );
    }
}


async function loadFavorites() {

    if (!loggedIn) {
        return;
    }

    try {

        const response = await fetch(
            `${API_BASE}/api/favorites`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {
            return;
        }

        const favorites =
            await response.json();

        favoriteMovieIds = new Set(
            favorites.map(movie => movie.id)
        );

    } catch (error) {

        console.error(
            "Error loading favorites:",
            error
        );
    }
}


async function toggleFavorite(movieId) {

    if (!loggedIn) {

        window.location.href =
            "login.html";

        return;
    }

    const isFavorite =
        favoriteMovieIds.has(movieId);

    try {

        const response = await fetch(
            `${API_BASE}/api/favorites/${movieId}`,
            {
                method:
                    isFavorite
                        ? "DELETE"
                        : "POST",

                credentials: "include"
            }
        );

        if (!response.ok) {
            return;
        }

        if (isFavorite) {

            favoriteMovieIds.delete(movieId);

        } else {

            favoriteMovieIds.add(movieId);
        }

        const button =
            document.querySelector(
                `[data-favorite-id="${movieId}"]`
            );

        if (button) {

            button.textContent =
                favoriteMovieIds.has(movieId)
                    ? "♥"
                    : "♡";
        }

    } catch (error) {

        console.error(
            "Favorite error:",
            error
        );
    }
}


function displayMovies(movies) {

    movieGrid.innerHTML = "";

    if (movies.length === 0) {

        noResults.style.display = "block";
        return;
    }

    noResults.style.display = "none";

    movies.forEach(function (movie) {

        const card =
            document.createElement("article");

        card.classList.add("browse-card");


        const image =
            document.createElement("img");

        image.src = movie.posterUrl;
        image.alt = movie.title;


        const content =
            document.createElement("div");

        content.classList.add(
            "browse-card-content"
        );


        const title =
            document.createElement("h3");

        title.textContent =
            movie.title;


        const genre =
            document.createElement("p");

        genre.textContent =
            movie.genre;


        const detailsLink =
            document.createElement("a");

        detailsLink.textContent =
            "Show Movie Details →";

        detailsLink.classList.add(
            "details-button"
        );

        detailsLink.href =
            `details.html?id=${movie.id}`;


        const favoriteButton =
            document.createElement("button");

        favoriteButton.type =
            "button";

        favoriteButton.classList.add(
            "favorite-button"
        );

        favoriteButton.dataset.favoriteId =
            movie.id;

        favoriteButton.title =
            "Add or remove favorite";

        favoriteButton.textContent =
            favoriteMovieIds.has(movie.id)
                ? "♥"
                : "♡";

        favoriteButton.addEventListener(
            "click",
            function () {

                toggleFavorite(movie.id);
            }
        );


        content.appendChild(title);
        content.appendChild(genre);
        content.appendChild(detailsLink);
        content.appendChild(favoriteButton);

        card.appendChild(image);
        card.appendChild(content);

        movieGrid.appendChild(card);
    });
}


async function loadAllMovies() {

    try {

        const response = await fetch(
            `${API_BASE}/api/movies`
        );

        const movies =
            await response.json();

        displayMovies(movies);

    } catch (error) {

        console.error(
            "Error loading movies:",
            error
        );
    }
}


searchInput.addEventListener(
    "input",
    function () {

        const searchText =
            searchInput.value
                .trim()
                .toLowerCase();

        fetch(
            `${API_BASE}/api/movies`
        )
            .then(response =>
                response.json()
            )
            .then(movies => {

                const filteredMovies =
                    movies.filter(movie =>
                        movie.title
                            .toLowerCase()
                            .includes(searchText)
                    );

                displayMovies(
                    filteredMovies
                );
            })
            .catch(error => {

                console.error(
                    "Error searching movies:",
                    error
                );
            });
    }
);


genreSelect.addEventListener(
    "change",
    function () {

        const selectedGenre =
            genreSelect.value
                .toLowerCase();

        fetch(
            `${API_BASE}/api/movies`
        )
            .then(response =>
                response.json()
            )
            .then(movies => {

                if (
                    selectedGenre === "all"
                ) {

                    displayMovies(movies);
                    return;
                }

                const filteredMovies =
                    movies.filter(movie =>
                        movie.genre
                            .toLowerCase() ===
                        selectedGenre
                    );

                displayMovies(
                    filteredMovies
                );
            })
            .catch(error => {

                console.error(
                    "Error filtering movies:",
                    error
                );
            });
    }
);


loginButton.addEventListener(
    "click",
    function () {

        window.location.href =
            "login.html";
    }
);


logoutButton.addEventListener(
    "click",
    async function () {

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
);


document.addEventListener(
    "DOMContentLoaded",
    async function () {

        await checkSession();
        await loadAllMovies();
    }
);