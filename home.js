fetch("http://localhost:8080/api/movies")
    .then(response => response.json())
    .then(movies => {

        const featuredContainer =
            document.getElementById("featured-movies");

        const comingSoonContainer =
            document.getElementById("coming-soon-movies");

        movies.forEach(function(movie) {

            console.log(movie.title, movie.status);
            const card = document.createElement("div");
            card.classList.add("card");

            const image = document.createElement("img");
            image.src = movie.posterUrl;
            image.alt = movie.title;
            image.width = 100;

            const title = document.createElement("h3");
            title.textContent = movie.title;

            const description = document.createElement("h5");
            description.textContent = movie.description;

            const button = document.createElement("button");
            button.textContent = "See Movie Details";
            button.classList.add("time");

            button.addEventListener("click", function() {
                window.location.href =
                    `details.html?id=${movie.id}`;
            });

            card.appendChild(image);
            card.appendChild(title);
            card.appendChild(description);
            card.appendChild(button);

            if (movie.status === "Currently Running") {
                featuredContainer.appendChild(card);
            }

            if (movie.status === "Coming Soon") {
                comingSoonContainer.appendChild(card);
            }
        });
    })
    .catch(error => {
        console.error("Error loading movies:", error);
    });