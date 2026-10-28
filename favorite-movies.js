const API_BASE = "http://localhost:8080";

const favoritesGrid = document.getElementById("favorites-grid");
const noFavorites = document.getElementById("no-favorites");

// Load the logged-in user's favorite movies
async function loadFavorites() {

    try {
        // Check whether the user is logged in
        const sessionResponse = await fetch(
            `${API_BASE}/api/auth/session`,
            {
                credentials: "include"
            }
        );

        if (!sessionResponse.ok) {
            throw new Error("Unable to check login session");
        }

        const session = await sessionResponse.json();

        if (!session.loggedIn) {
            window.location.href = "login.html";
            return;
        }

        // Retrieve saved favorites from H2
        const response = await fetch(
            `${API_BASE}/api/favorites`,
            {
                credentials: "include"
            }
        );

        if (response.status === 401) {
            window.location.href = "login.html";
            return;
        }

        if (!response.ok) {
            throw new Error("Unable to retrieve favorites");
        }

        const favorites = await response.json();

        displayFavorites(favorites);

    } catch (error) {
        console.error("Error loading favorites:", error);

        favoritesGrid.innerHTML = "";
        noFavorites.textContent =
            "Unable to load your favorites. Please try again.";
        noFavorites.style.display = "block";
    }
}

// Display favorite movies
function displayFavorites(favorites) {

    favoritesGrid.innerHTML = "";

    if (favorites.length === 0) {
        noFavorites.textContent =
            "You haven't added any favorite movies yet.";
        noFavorites.style.display = "block";
        return;
    }

    noFavorites.style.display = "none";

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
        detailsLink.href =
            `details.html?id=${encodeURIComponent(movie.id)}`;

        const removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.textContent = "♥ Remove from Favorites";
        removeButton.classList.add("favorite-button", "is-favorite");

        removeButton.addEventListener("click", async function() {

            removeButton.disabled = true;

            try {
                const response = await fetch(
                    `${API_BASE}/api/favorites/${encodeURIComponent(movie.id)}`,
                    {
                        method: "DELETE",
                        credentials: "include"
                    }
                );

                if (response.status === 401) {
                    window.location.href = "login.html";
                    return;
                }

                if (!response.ok) {
                    throw new Error("Unable to remove favorite");
                }

                // Refresh favorites after removing
                await loadFavorites();

            } catch (error) {
                console.error("Error removing favorite:", error);
                alert("Could not remove favorite. Please try again.");
                removeButton.disabled = false;
            }
        });

        content.appendChild(title);
        content.appendChild(detailsLink);
        content.appendChild(removeButton);

        card.appendChild(image);
        card.appendChild(content);

        favoritesGrid.appendChild(card);
    });
}

// Initialize the favorites page
document.addEventListener("DOMContentLoaded", loadFavorites);