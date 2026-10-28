const API_BASE = "http://localhost:8080";


async function loadProfile() {

    const message =
        document.getElementById("profileMessage");

    try {

        const sessionResponse = await fetch(
            `${API_BASE}/api/auth/session`,
            {
                credentials: "include"
            }
        );


        if (!sessionResponse.ok) {

            throw new Error(
                "Could not check login session."
            );
        }


        const session =
            await sessionResponse.json();


        if (!session.loggedIn) {

            window.location.href =
                "login.html";

            return;
        }


        const response = await fetch(
            `${API_BASE}/api/profile`,
            {
                method: "GET",
                credentials: "include"
            }
        );


        if (!response.ok) {

            throw new Error(
                "Could not load profile."
            );
        }


        const user =
            await response.json();


        const welcomeMessage =
            document.getElementById(
                "welcomeMessage"
            );


        if (welcomeMessage) {

            welcomeMessage.textContent =
                `Welcome, ${user.firstName}!`;
        }


        const fields = [
            "firstName",
            "lastName",
            "email",
            "phoneNumber",
            "address",
            "city",
            "state",
            "zipCode"
        ];


        fields.forEach(function (field) {

            const input =
                document.getElementById(field);


            if (input) {

                input.value =
                    user[field] ?? "";
            }
        });


        const promotions =
            document.getElementById(
                "subscribedToPromotions"
            );


        if (promotions) {

            promotions.checked =
                user.subscribedToPromotions ??
                false;
        }


        if (message) {

            message.textContent = "";
        }

    } catch (error) {

        console.error(
            "Profile load error:",
            error
        );


        if (message) {

            message.textContent =
                "Unable to load your profile.";
        }
    }
}


async function saveProfile(event) {

    event.preventDefault();


    const message =
        document.getElementById(
            "profileMessage"
        );


    const updatedProfile = {

        firstName:
            document
                .getElementById("firstName")
                .value
                .trim(),

        lastName:
            document
                .getElementById("lastName")
                .value
                .trim(),

        phoneNumber:
            document
                .getElementById("phoneNumber")
                .value
                .trim(),

        address:
            document
                .getElementById("address")
                .value
                .trim(),

        city:
            document
                .getElementById("city")
                .value
                .trim(),

        state:
            document
                .getElementById("state")
                .value
                .trim(),

        zipCode:
            document
                .getElementById("zipCode")
                .value
                .trim(),

        subscribedToPromotions:
            document
                .getElementById(
                    "subscribedToPromotions"
                )
                .checked
    };


    message.textContent =
        "Saving changes...";


    try {

        const response = await fetch(
            `${API_BASE}/api/profile`,
            {
                method: "PUT",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                credentials: "include",

                body: JSON.stringify(
                    updatedProfile
                )
            }
        );


        let data = null;

        try {

            data = await response.json();

        } catch (error) {

            data = null;
        }


        if (!response.ok) {

            message.textContent =
                data?.message ||
                "Unable to save your changes.";

            return;
        }


        message.textContent =
            "Profile updated successfully!";

    } catch (error) {

        console.error(
            "Save profile error:",
            error
        );


        message.textContent =
            "Unable to save your changes. Please try again.";
    }
}


async function loadPaymentCards() {

    const savedCards =
        document.getElementById(
            "saved-cards"
        );


    if (!savedCards) {
        return;
    }


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


        const cards =
            await response.json();


        savedCards.innerHTML = "";


        if (cards.length === 0) {

            savedCards.innerHTML =
                "<p>No saved cards.</p>";

            return;
        }


        cards.forEach(function (card) {

            const cardElement =
                document.createElement(
                    "div"
                );


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


            savedCards.appendChild(
                cardElement
            );
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
        document.getElementById(
            "cardMessage"
        );


    const cardData = {

        cardholderName:
            document
                .getElementById(
                    "cardholderName"
                )
                .value
                .trim(),

        cardNumber:
            document
                .getElementById(
                    "cardNumber"
                )
                .value
                .trim(),

        expirationMonth:
            Number(
                document
                    .getElementById(
                        "expirationMonth"
                    )
                    .value
            ),

        expirationYear:
            Number(
                document
                    .getElementById(
                        "expirationYear"
                    )
                    .value
            )
    };


    try {

        const response = await fetch(
            `${API_BASE}/api/payment-cards`,
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                credentials: "include",

                body: JSON.stringify(
                    cardData
                )
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

            return;
        }


        let message =
            "Unable to add payment card.";


        try {

            const data =
                await response.json();

            message =
                data.message || message;

        } catch (error) {

            try {

                const text =
                    await response.text();

                if (text) {
                    message = text;
                }

            } catch (ignored) {
            }
        }


        cardMessage.textContent =
            message;

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


        if (!response.ok) {

            throw new Error(
                "Unable to delete payment card."
            );
        }


        loadPaymentCards();

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


    if (!favoriteMovies) {
        return;
    }


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
                document.createElement(
                    "div"
                );


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


document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadProfile();
        loadPaymentCards();
        loadFavoriteMovies();


        const profileForm =
            document.getElementById(
                "editProfileForm"
            );


        if (profileForm) {

            profileForm.addEventListener(
                "submit",
                saveProfile
            );
        }


        const paymentForm =
            document.getElementById(
                "payment-card-form"
            );


        if (paymentForm) {

            paymentForm.addEventListener(
                "submit",
                addPaymentCard
            );
        }
    }
);