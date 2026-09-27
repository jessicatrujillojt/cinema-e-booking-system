const searchInput = document.getElementById("movie-search");
const genreSelect = document.getElementById("genre");
const movieCards = document.querySelectorAll(".browse-card");
const noResults = document.getElementById("no-results");

function filterMovies() {

    const searchText = searchInput.value.toLowerCase().trim();
    const selectedGenre = genreSelect.value.toLowerCase();

    let matchingMovies = 0;

    movieCards.forEach(function(card) {

        const movieTitle = card.dataset.title.toLowerCase();
        const movieGenre = card.dataset.genre.toLowerCase();

        const titleMatches = movieTitle.includes(searchText);

        const genreMatches =
            selectedGenre === "all" ||
            movieGenre === selectedGenre;

        if (titleMatches && genreMatches) {

            card.style.display = "";

            matchingMovies++;

        } else {

            card.style.display = "none";

        }
    });

    if (matchingMovies === 0) {

        noResults.style.display = "block";

    } else {

        noResults.style.display = "none";

    }
}

searchInput.addEventListener("input", filterMovies);
genreSelect.addEventListener("change", filterMovies);
