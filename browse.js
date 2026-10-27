const searchInput = document.getElementById("movie-search");
const genreSelect = document.getElementById("genre");
const movieGrid = document.getElementById("movie-grid");
const noResults = document.getElementById("no-results");

function displayMovies(movies) {
    movieGrid.innerHTML = "";

    if (movies.length === 0) {
        noResults.style.display = "block";
        return;
    }

    noResults.style.display = "none";

    movies.forEach(function(movie) {
        const card = document.createElement("article");
        card.classList.add("browse-card");

        const image = document.createElement("img");
        image.src = movie.posterUrl;
        image.alt = movie.title;

        const content = document.createElement("div");
        content.classList.add("browse-card-content");

        const title = document.createElement("h3");
        title.textContent = movie.title;

        const genre = document.createElement("p");
        genre.textContent = movie.genre;

        const detailsLink = document.createElement("a");
        detailsLink.textContent = "Show Movie Details →";
        detailsLink.classList.add("details-button");
        detailsLink.href = `details.html?id=${movie.id}`;
        
const favoriteButton = document.createElement("button");
favoriteButton.type = "button";
favoriteButton.classList.add("favorite-button");
favoriteButton.setAttribute("aria-label", "Add to Favorites");
favoriteButton.title = "Add to Favorites";
favoriteButton.textContent = "♡";
const savedFavorites = JSON.parse(
    localStorage.getItem("favoriteMovies") || "[]"
);

const alreadyFavorite = savedFavorites.some(
    favorite => String(favorite.id) === String(movie.id)
);

if (alreadyFavorite) {
    favoriteButton.textContent = "♥";
    favoriteButton.title = "Remove from Favorites";
    favoriteButton.setAttribute("aria-label", "Remove from Favorites");
    favoriteButton.classList.add("is-favorite");
} else {
    favoriteButton.title = "Add to Favorites";
    favoriteButton.setAttribute("aria-label", "Add to Favorites");
}
favoriteButton.addEventListener("click", function() {
    const favorites = JSON.parse(
        localStorage.getItem("favoriteMovies") || "[]"
    );

    const isFavorite = favorites.some(
        favorite => String(favorite.id) === String(movie.id)
    );

    if (isFavorite) {
        const updatedFavorites = favorites.filter(
            favorite => String(favorite.id) !== String(movie.id)
        );
        localStorage.setItem(
            "favoriteMovies",
            JSON.stringify(updatedFavorites)
        );
        favoriteButton.textContent = "♡";
        favoriteButton.title = "Add to Favorites";
        favoriteButton.setAttribute("aria-label", "Add to Favorites");
        favoriteButton.classList.remove("is-favorite");
    } else {
        favorites.push(movie);
        localStorage.setItem(
            "favoriteMovies",
            JSON.stringify(favorites)
        );
        favoriteButton.textContent = "♥";
        favoriteButton.title = "Remove from Favorites";
        favoriteButton.setAttribute("aria-label", "Remove from Favorites");
        favoriteButton.classList.add("is-favorite");
    }
});

        content.appendChild(title);
        content.appendChild(genre);
        content.appendChild(detailsLink);

        card.appendChild(image);
        card.appendChild(content);
        card.appendChild(favoriteButton);
        movieGrid.appendChild(card);
    });
}

function loadAllMovies() {
    fetch("http://localhost:8080/api/movies")
        .then(response => response.json())
        .then(movies => {
            displayMovies(movies);
        })
        .catch(error => {
            console.error("Error loading movies:", error);
        });
}

searchInput.addEventListener("input", function() {
    const searchText = searchInput.value.trim();

    if (searchText === "") {
        loadAllMovies();
        return;
    }

    fetch(
        `http://localhost:8080/api/movies/search?title=${encodeURIComponent(searchText)}`
    )
        .then(response => response.json())
        .then(movies => {
            displayMovies(movies);
        })
        .catch(error => {
            console.error("Error searching movies:", error);
        });
});

genreSelect.addEventListener("change", function() {
    const selectedGenre = genreSelect.value;

    if (selectedGenre === "all") {
        loadAllMovies();
        return;
    }

    fetch(
        `http://localhost:8080/api/movies/genre?genre=${encodeURIComponent(selectedGenre)}`
    )
        .then(response => response.json())
        .then(movies => {
            displayMovies(movies);
        })
        .catch(error => {
            console.error("Error filtering movies:", error);
        });
});

loadAllMovies();