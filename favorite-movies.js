
const favoritesGrid = document.getElementById("favorites-grid");
const noFavorites = document.getElementById("no-favorites");

function displayFavorites() {
    const favorites = JSON.parse(
        localStorage.getItem("favoriteMovies") || "[]"
    );

    favoritesGrid.innerHTML = "";
    noFavorites.style.display = favorites.length === 0 ? "block" : "none";

    favorites.forEach(movie => {
        const card = document.createElement("article");
        card.classList.add("browse-card");

        const image = document.createElement("img");
        image.src = movie.posterUrl || "";
        image.alt = movie.title || "Movie poster";

        const content = document.createElement("div");
        content.classList.add("browse-card-content");

        const title = document.createElement("h3");
        title.textContent = movie.title;

        const detailsLink = document.createElement("a");
        detailsLink.textContent = "Show Movie Details →";
        detailsLink.classList.add("details-button");
        detailsLink.href = `details.html?id=${movie.id}`;

        const removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.textContent = "♥ Remove from Favorites";
        removeButton.classList.add("favorite-button");

        removeButton.addEventListener("click", function() {
            const updatedFavorites = favorites.filter(
                favorite => String(favorite.id) !== String(movie.id)
            );

            localStorage.setItem(
                "favoriteMovies",
                JSON.stringify(updatedFavorites)
            );

            displayFavorites();
        });

        content.appendChild(title);
        content.appendChild(detailsLink);
        content.appendChild(removeButton);

        card.appendChild(image);
        card.appendChild(content);
        favoritesGrid.appendChild(card);
    });
}

displayFavorites();
