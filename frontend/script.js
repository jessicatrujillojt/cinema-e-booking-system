const API_URL = "http://localhost:8080/api/movies";

async function loadMovies() {
try {
const response = await fetch(API_URL);

    if (!response.ok) {
        throw new Error("Could not load movies.");
    }

    const movies = await response.json();

    const currentlyRunning = document.getElementById("currently-running");
    const comingSoon = document.getElementById("coming-soon");

    currentlyRunning.innerHTML = "";
    comingSoon.innerHTML = "";

    movies.forEach(movie => {
        const card = document.createElement("div");
        card.className = "card";

        card.innerHTML = `
            <img
                src="${movie.posterUrl}"
                alt="${movie.title} Poster"
                style="width: 100%; height: 250px; object-fit: cover;"
            >

            <h4>${movie.title}</h4>

            <h5>${movie.description}</h5>


            <button
                type="button"
                onclick="viewMovie(${movie.id})">
                See Movie Details
            </button>
        `;

        if (movie.status === "Currently Running") {
            currentlyRunning.appendChild(card);
        } else if (movie.status === "Coming Soon") {
            comingSoon.appendChild(card);
        }
    });

} catch (error) {
    console.error("Error loading movies:", error);

    document.getElementById("currently-running").innerHTML =
        "<p>Unable to load movies. Please make sure the backend is running.</p>";

    document.getElementById("coming-soon").innerHTML =
        "<p>Unable to load movies. Please make sure the backend is running.</p>";
}

}

function viewMovie(movieId) {
window.location.href = `details.html?id=${movieId}`;
}

loadMovies();