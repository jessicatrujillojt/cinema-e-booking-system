const API_BASE = "http://localhost:8080";

const searchInput =
    document.getElementById("movie-search");

const genreSelect =
    document.getElementById("genre");

const movieGrid =
    document.getElementById("movie-grid");

const noResults =
    document.getElementById("no-results");


let favoriteIds = new Set();
let loggedIn = false;
let allMovies = [];


/*
 * Check whether the user is logged in
 * and load their saved favorite movies.
 */
async function loadFavorites() {

    try {

        const sessionResponse = await fetch(
            `${API_BASE}/api/auth/session`,
            {
                credentials: "include"
            }
        );

        if (!sessionResponse.ok) {

            loggedIn = false;
            favoriteIds.clear();

            return;
        }

        const session =
            await sessionResponse.json();

        loggedIn =
            session.loggedIn === true;

        favoriteIds.clear();


        if (!loggedIn) {
            return;
        }


        const response = await fetch(
            `${API_BASE}/api/favorites`,
            {
                credentials: "include"
            }
        );


        if (!response.ok) {

            throw new Error(
                "Unable to load favorites."
            );
        }


        const favorites =
            await response.json();


        favoriteIds = new Set(
            favorites.map(
                movie => String(movie.id)
            )
        );

    } catch (error) {

        console.error(
            "Error loading favorites:",
            error
        );

        loggedIn = false;
        favoriteIds.clear();
    }
}


/*
 * Update the heart icon for a movie.
 */
function updateHeart(button, movieId) {

    const isFavorite =
        favoriteIds.has(
            String(movieId)
        );


    button.textContent =
        isFavorite
            ? "♥"
            : "♡";


    button.title =
        isFavorite
            ? "Remove from Favorites"
            : "Add to Favorites";


    button.setAttribute(
        "aria-label",
        button.title
    );


    button.classList.toggle(
        "is-favorite",
        isFavorite
    );
}


/*
 * Add or remove a movie from favorites.
 */
async function toggleFavorite(
    movieId,
    button
) {

    if (!loggedIn) {

        alert(
            "Please log in to save your favorite movies."
        );

        window.location.href =
            "login.html";

        return;
    }


    const id =
        String(movieId);

    const alreadyFavorite =
        favoriteIds.has(id);


    button.disabled = true;


    try {

        const response = await fetch(
            `${API_BASE}/api/favorites/${encodeURIComponent(id)}`,
            {
                method:
                    alreadyFavorite
                        ? "DELETE"
                        : "POST",

                credentials: "include"
            }
        );


        if (response.status === 401) {

            loggedIn = false;

            window.location.href =
                "login.html";

            return;
        }


        if (!response.ok) {

            throw new Error(
                "Unable to update favorites."
            );
        }


        if (alreadyFavorite) {

            favoriteIds.delete(id);

        } else {

            favoriteIds.add(id);
        }


        document
            .querySelectorAll(
                ".favorite-button"
            )
            .forEach(function (heart) {

                updateHeart(
                    heart,
                    heart.dataset.movieId
                );
            });

    } catch (error) {

        console.error(
            "Favorite error:",
            error
        );

        alert(
            "Could not update favorites. Please try again."
        );

    } finally {

        button.disabled = false;
    }
}


/*
 * Display the movie cards.
 */
function displayMovies(movies) {

    movieGrid.innerHTML = "";


    if (movies.length === 0) {

        noResults.style.display =
            "block";

        return;
    }


    noResults.style.display =
        "none";


    movies.forEach(function (movie) {

        const card =
            document.createElement(
                "article"
            );

        card.classList.add(
            "browse-card"
        );


        const image =
            document.createElement(
                "img"
            );

        image.src =
            movie.posterUrl;

        image.alt =
            movie.title;


        const content =
            document.createElement(
                "div"
            );

        content.classList.add(
            "browse-card-content"
        );


        const title =
            document.createElement(
                "h3"
            );

        title.textContent =
            movie.title;


        const genre =
            document.createElement(
                "p"
            );

        genre.textContent =
            movie.genre;


        const detailsLink =
            document.createElement(
                "a"
            );

        detailsLink.textContent =
            "Show Movie Details →";

        detailsLink.classList.add(
            "details-button"
        );

        detailsLink.href =
            `details.html?id=${encodeURIComponent(movie.id)}`;


        const favoriteButton =
            document.createElement(
                "button"
            );

        favoriteButton.type =
            "button";

        favoriteButton.classList.add(
            "favorite-button"
        );

        favoriteButton.dataset.movieId =
            movie.id;


        updateHeart(
            favoriteButton,
            movie.id
        );


        favoriteButton.addEventListener(
            "click",
            function () {

                toggleFavorite(
                    movie.id,
                    favoriteButton
                );
            }
        );


        content.appendChild(title);
        content.appendChild(genre);
        content.appendChild(detailsLink);
        content.appendChild(
            favoriteButton
        );

        card.appendChild(image);
        card.appendChild(content);

        movieGrid.appendChild(card);
    });
}


/*
 * Load all movies from the backend.
 */
async function loadAllMovies() {

    try {

        const response = await fetch(
            `${API_BASE}/api/movies`
        );


        if (!response.ok) {

            throw new Error(
                "Unable to retrieve movies."
            );
        }


        allMovies =
            await response.json();


        displayMovies(allMovies);

    } catch (error) {

        console.error(
            "Error loading movies:",
            error
        );

        movieGrid.innerHTML = "";

        noResults.style.display =
            "block";
    }
}


/*
 * Search movies by title.
 */
searchInput.addEventListener(
    "input",
    function () {

        const searchText =
            searchInput.value
                .trim()
                .toLowerCase();


        const filteredMovies =
            allMovies.filter(
                function (movie) {

                    return movie.title
                        .toLowerCase()
                        .includes(
                            searchText
                        );
                }
            );


        displayMovies(
            filteredMovies
        );
    }
);


/*
 * Filter movies by genre.
 */
genreSelect.addEventListener(
    "change",
    function () {

        const selectedGenre =
            genreSelect.value
                .toLowerCase();


        if (
            selectedGenre === "all"
        ) {

            displayMovies(
                allMovies
            );

            return;
        }


        const filteredMovies =
            allMovies.filter(
                function (movie) {

                    return movie.genre
                        .toLowerCase() ===
                        selectedGenre;
                }
            );


        displayMovies(
            filteredMovies
        );
    }
);


/*
 * Initialize the browse page.
 */
document.addEventListener(
    "DOMContentLoaded",
    async function () {

        await loadFavorites();
        await loadAllMovies();
    }
);