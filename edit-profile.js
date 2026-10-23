const API_BASE = "http://localhost:8080";


async function loadProfile() {

    const message =
        document.getElementById("profileMessage");

    try {

        const response = await fetch(
            `${API_BASE}/api/profile`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {

            if (response.status === 401) {
                window.location.href = "login.html";
                return;
            }

            throw new Error(
                "Could not load your profile."
            );
        }

        const user = await response.json();

        document.getElementById("firstName").value =
            user.firstName ?? "";

        document.getElementById("lastName").value =
            user.lastName ?? "";

        document.getElementById("email").value =
            user.email ?? "";

        document.getElementById("phoneNumber").value =
            user.phoneNumber ?? "";

        document.getElementById("address").value =
            user.address ?? "";

        document.getElementById("city").value =
            user.city ?? "";

        document.getElementById("state").value =
            user.state ?? "";

        document.getElementById("zipCode").value =
            user.zipCode ?? "";

        document.getElementById(
            "subscribedToPromotions"
        ).checked =
            user.subscribedToPromotions ?? false;

        message.textContent = "";

    } catch (error) {

        console.error(
            "Load profile error:",
            error
        );

        message.textContent =
            "Unable to load your profile. Please try again.";
    }
}


async function updateProfile(event) {

    event.preventDefault();

    const message =
        document.getElementById("profileMessage");

    const profileData = {

        firstName:
            document.getElementById("firstName").value,

        lastName:
            document.getElementById("lastName").value,

        phoneNumber:
            document.getElementById("phoneNumber").value,

        address:
            document.getElementById("address").value,

        city:
            document.getElementById("city").value,

        state:
            document.getElementById("state").value,

        zipCode:
            document.getElementById("zipCode").value,

        subscribedToPromotions:
            document.getElementById(
                "subscribedToPromotions"
            ).checked
    };

    try {

        const response = await fetch(
            `${API_BASE}/api/profile`,
            {
                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                credentials: "include",

                body: JSON.stringify(profileData)
            }
        );

        const data = await response.json();

        if (response.ok) {

            message.textContent =
                "Profile updated successfully.";

        } else {

            message.textContent =
                data.message ||
                "Unable to update profile.";
        }

    } catch (error) {

        console.error(
            "Update profile error:",
            error
        );

        message.textContent =
            "Unable to connect to the server.";
    }
}


async function loadPaymentCards() {

    const savedCards =
        document.getElementById("saved-cards");

    try {

        const response = await fetch(
            `${API_BASE}/api/payment-cards`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error(
                "Could not load cards."
            );
        }

        const cards = await response.json();

        savedCards.innerHTML = "";

        if (cards.length === 0) {

            savedCards.innerHTML =
                "<p>No saved cards.</p>";

            return;
        }

        cards.forEach(function (card) {

            const cardElement =
                document.createElement("div");

            cardElement.innerHTML = `
                <p>
                    ${card.cardholderName}
                    - Card ending in ${card.lastFour}
                    - Expires ${card.expirationMonth}/${card.expirationYear}

                    <button
                        type="button"
                        onclick="deletePaymentCard(${card.id})"
                    >
                        Delete
                    </button>
                </p>
            `;

            savedCards.appendChild(cardElement);
        });

    } catch (error) {

        console.error(
            "Load cards error:",
            error
        );

        savedCards.innerHTML =
            "<p>Unable to load saved cards.</p>";
    }
}


async function addPaymentCard(event) {

    event.preventDefault();

    const cardMessage =
        document.getElementById("cardMessage");

    const cardData = {

        cardholderName:
            document.getElementById(
                "cardholderName"
            ).value,

        cardNumber:
            document.getElementById(
                "cardNumber"
            ).value,

        expirationMonth:
            Number(
                document.getElementById(
                    "expirationMonth"
                ).value
            ),

        expirationYear:
            Number(
                document.getElementById(
                    "expirationYear"
                ).value
            )
    };

    try {

        const response = await fetch(
            `${API_BASE}/api/payment-cards`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                credentials: "include",

                body: JSON.stringify(cardData)
            }
        );

        if (response.ok) {

            cardMessage.textContent =
                "Payment card added successfully.";

            document
                .getElementById(
                    "payment-card-form"
                )
                .reset();

            loadPaymentCards();

        } else {

            const message =
                await response.text();

            cardMessage.textContent =
                message ||
                "Unable to add payment card.";
        }

    } catch (error) {

        console.error(
            "Add card error:",
            error
        );

        cardMessage.textContent =
            "Unable to connect to the server.";
    }
}


async function deletePaymentCard(cardId) {

    try {

        const response = await fetch(
            `${API_BASE}/api/payment-cards/${cardId}`,
            {
                method: "DELETE",
                credentials: "include"
            }
        );

        if (response.ok) {

            loadPaymentCards();

        }

    } catch (error) {

        console.error(
            "Delete card error:",
            error
        );
    }
}


async function loadFavoriteMovies() {

    const favoriteMovies =
        document.getElementById(
            "favorite-movies"
        );

    try {

        const response = await fetch(
            `${API_BASE}/api/favorites`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error(
                "Could not load favorites."
            );
        }

        const movies =
            await response.json();

        favoriteMovies.innerHTML = "";

        if (movies.length === 0) {

            favoriteMovies.innerHTML =
                "<p>No favorite movies yet.</p>";

            return;
        }

        movies.forEach(function (movie) {

            const movieElement =
                document.createElement("div");

            movieElement.innerHTML = `
                <p>
                    <strong>${movie.title}</strong>
                    - ${movie.genre}
                </p>
            `;

            favoriteMovies.appendChild(
                movieElement
            );
        });

    } catch (error) {

        console.error(
            "Load favorites error:",
            error
        );

        favoriteMovies.innerHTML =
            "<p>Unable to load favorite movies.</p>";
    }
}


async function logout(event) {

    event.preventDefault();

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


document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadProfile();
        loadPaymentCards();
        loadFavoriteMovies();

        document
            .getElementById(
                "editProfileForm"
            )
            .addEventListener(
                "submit",
                updateProfile
            );

        document
            .getElementById(
                "payment-card-form"
            )
            .addEventListener(
                "submit",
                addPaymentCard
            );

        document
            .getElementById(
                "logout-link"
            )
            .addEventListener(
                "click",
                logout
            );
    }
);