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

        content.appendChild(title);
        content.appendChild(genre);
        content.appendChild(detailsLink);

        card.appendChild(image);
        card.appendChild(content);

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