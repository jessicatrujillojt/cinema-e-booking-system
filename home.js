const API_BASE = "http://localhost:8080";

const loginButton =
    document.getElementById("login-button");

const logoutButton =
    document.getElementById("logout-button");

const profileLinkContainer =
    document.getElementById("profile-link-container");


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

        if (data.loggedIn === true) {

            loginButton.style.display = "none";
            logoutButton.style.display = "inline-block";
            profileLinkContainer.style.display = "block";

        } else {

            loginButton.style.display = "inline-block";
            logoutButton.style.display = "none";
            profileLinkContainer.style.display = "none";
        }

    } catch (error) {

        console.error(
            "Session check error:",
            error
        );

        loginButton.style.display = "inline-block";
        logoutButton.style.display = "none";
        profileLinkContainer.style.display = "none";
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


function loadMovies() {

    fetch(`${API_BASE}/api/movies`)

        .then(response =>
            response.json()
        )

        .then(movies => {

            const featuredContainer =
                document.getElementById(
                    "featured-movies"
                );

            const comingSoonContainer =
                document.getElementById(
                    "coming-soon-movies"
                );

            movies.forEach(function(movie) {

                console.log(
                    movie.title,
                    movie.status
                );

                const card =
                    document.createElement("div");

                card.classList.add("card");


                const image =
                    document.createElement("img");

                image.src =
                    movie.posterUrl;

                image.alt =
                    movie.title;

                image.width = 100;


                const title =
                    document.createElement("h3");

                title.textContent =
                    movie.title;


                const description =
                    document.createElement("h5");

                description.textContent =
                    movie.showtimes[0]
                        ? `Showtime: ${movie.showtimes[0]}`
                        : "No showtimes available";


                const button =
                    document.createElement("button");

                button.textContent =
                    "See Movie Details";

                button.classList.add("time");


                button.addEventListener(
                    "click",
                    function() {

                        window.location.href =
                            `details.html?id=${movie.id}`;
                    }
                );


                card.appendChild(image);
                card.appendChild(title);
                card.appendChild(description);
                card.appendChild(button);


                if (
                    movie.status ===
                    "Currently Running"
                ) {

                    featuredContainer
                        .appendChild(card);
                }


                if (
                    movie.status ===
                    "Coming Soon"
                ) {

                    comingSoonContainer
                        .appendChild(card);
                }
            });
        })

        .catch(error => {

            console.error(
                "Error loading movies:",
                error
            );
        });
}


loginButton.addEventListener(
    "click",
    function() {

        window.location.href =
            "login.html";
    }
);


logoutButton.addEventListener(
    "click",
    logout
);


document.addEventListener(
    "DOMContentLoaded",
    function() {

        checkSession();
        loadMovies();
    }
);