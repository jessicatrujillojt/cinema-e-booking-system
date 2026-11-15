const API_BASE = "http://localhost:8080";

const params = new URLSearchParams(window.location.search);

const showtimeId = Number(params.get("showtimeId"));
const movie = params.get("movie");
const date = params.get("date");
const time = params.get("time");
const poster = params.get("poster");

const rows = Number(params.get("rows"));
const seatsPerRow = Number(params.get("seatsPerRow"));


/* MOVIE INFORMATION */

if (movie) {
    document.getElementById("booking-movie-title").textContent =
        movie;
}

if (date) {
    document.getElementById("booking-date").textContent =
        date;
}

if (time) {
    document.getElementById("booking-time").textContent =
        time;
}

if (poster) {
    document.getElementById("booking-poster").src =
        poster;
}


/* TICKET PRICES */

const prices = {
    adult: 12.00,
    child: 8.00,
    senior: 10.00
};


/* TICKET QUANTITIES */

const quantities = {
    adult: 0,
    child: 0,
    senior: 0
};


let selectedSeats = [];

let unavailableSeats = new Set();

const seatMessage =
    document.getElementById("seat-message");


/* LOAD UNAVAILABLE SEATS */

async function loadUnavailableSeats() {

    try {

        const response = await fetch(
            `${API_BASE}/api/bookings/showtime/${showtimeId}/unavailable-seats`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error(
                "Failed to load unavailable seats."
            );
        }

        const seats = await response.json();

        unavailableSeats =
            new Set(seats);

        buildSeatMap();

    } catch (error) {

        console.error(
            "Load unavailable seats error:",
            error
        );

        seatMessage.textContent =
            "Unable to load seat availability.";
    }
}


/* BUILD SEAT MAP */

function buildSeatMap() {

    const seatLayout =
        document.querySelector(".seat-layout");

    seatLayout.innerHTML = "";

    for (let rowIndex = 0; rowIndex < rows; rowIndex++) {

        const rowLetter =
            String.fromCharCode(65 + rowIndex);

        const row =
            document.createElement("div");

        row.classList.add("seat-row");


        const rowLabel =
            document.createElement("span");

        rowLabel.classList.add("row-label");

        rowLabel.textContent =
            rowLetter;

        row.appendChild(rowLabel);


        for (
            let seatNumber = 1;
            seatNumber <= seatsPerRow;
            seatNumber++
        ) {

            const seatName =
                `${rowLetter}${seatNumber}`;

            const button =
                document.createElement("button");

            button.type = "button";

            button.classList.add("seat");

            button.dataset.seat =
                seatName;

            button.textContent =
                seatName;


            if (
                unavailableSeats.has(seatName)
            ) {

                button.classList.add("taken");

                button.disabled = true;
            }


            button.addEventListener(
                "click",
                function () {

                    selectSeat(button);
                }
            );


            row.appendChild(button);
        }


        seatLayout.appendChild(row);
    }
}


/* TICKET BUTTONS */

const quantityButtons =
    document.querySelectorAll(
        ".quantity-button"
    );


quantityButtons.forEach(
    function (button) {

        button.addEventListener(
            "click",
            function () {

                const type =
                    button.dataset.type;

                const action =
                    button.dataset.action;


                if (action === "plus") {

                    quantities[type]++;

                    seatMessage.textContent = "";

                } else if (
                    action === "minus"
                ) {

                    if (
                        quantities[type] > 0
                    ) {

                        const newMaximum =
                            getMaximumSeats() - 1;

                        if (
                            selectedSeats.length >
                            newMaximum
                        ) {

                            seatMessage.textContent =
                                "Deselect a seat before reducing the number of tickets.";

                            return;
                        }

                        quantities[type]--;
                    }
                }


                updateTicketDisplay();

                updateSummary();
            }
        );
    }
);


/* TOTAL NUMBER OF TICKETS */

function getMaximumSeats() {

    return (
        quantities.adult +
        quantities.child +
        quantities.senior
    );
}


/* UPDATE TICKET COUNTERS */

function updateTicketDisplay() {

    document
        .getElementById(
            "adult-quantity"
        )
        .textContent =
        quantities.adult;


    document
        .getElementById(
            "child-quantity"
        )
        .textContent =
        quantities.child;


    document
        .getElementById(
            "senior-quantity"
        )
        .textContent =
        quantities.senior;
}


/* SELECT SEAT */

function selectSeat(seat) {

    if (
        seat.classList.contains("taken")
    ) {
        return;
    }


    const seatName =
        seat.dataset.seat;


    if (
        selectedSeats.includes(
            seatName
        )
    ) {

        selectedSeats =
            selectedSeats.filter(
                function (selectedSeat) {

                    return (
                        selectedSeat !==
                        seatName
                    );
                }
            );

        seat.classList.remove(
            "selected"
        );

        seatMessage.textContent = "";

    } else {

        const maximumSeats =
            getMaximumSeats();


        if (maximumSeats === 0) {

            seatMessage.textContent =
                "Select at least one ticket before choosing seats.";

            return;
        }


        if (
            selectedSeats.length >=
            maximumSeats
        ) {

            seatMessage.textContent =
                "You can only select " +
                maximumSeats +
                " seat(s).";

            return;
        }


        selectedSeats.push(
            seatName
        );

        seat.classList.add(
            "selected"
        );

        seatMessage.textContent = "";
    }


    updateSummary();
}


/* SUMMARY */

function updateSummary() {

    const selectedSeatsElement =
        document.getElementById(
            "selected-seats"
        );

    const totalPriceElement =
        document.getElementById(
            "total-price"
        );


    if (
        selectedSeats.length === 0
    ) {

        selectedSeatsElement.textContent =
            "None";

    } else {

        selectedSeatsElement.textContent =
            selectedSeats.join(", ");
    }


    const total =
        (
            quantities.adult *
            prices.adult
        ) +
        (
            quantities.child *
            prices.child
        ) +
        (
            quantities.senior *
            prices.senior
        );


    totalPriceElement.textContent =
        "$" + total.toFixed(2);
}


/* CREATE BOOKING */

async function createBooking() {

    const response = await fetch(
        `${API_BASE}/api/bookings`,
        {
            method: "POST",

            headers: {
                "Content-Type":
                    "application/json"
            },

            credentials: "include",

            body: JSON.stringify({
                showtimeId:
                    showtimeId,

                adultTickets:
                    quantities.adult,

                childTickets:
                    quantities.child,

                seniorTickets:
                    quantities.senior
            })
        }
    );


    if (!response.ok) {

        const error =
            await response.json();

        throw new Error(
            error.error ||
            "Unable to create booking."
        );
    }


    return await response.json();
}


/* RESERVE SEATS */

async function reserveSeats(
    bookingId
) {

    const response = await fetch(
        `${API_BASE}/api/bookings/${bookingId}/seats`,
        {
            method: "POST",

            headers: {
                "Content-Type":
                    "application/json"
            },

            credentials: "include",

            body: JSON.stringify({
                selectedSeats:
                    selectedSeats
            })
        }
    );


    if (!response.ok) {

        const error =
            await response.json();

        throw new Error(
            error.error ||
            "Unable to reserve seats."
        );
    }


    return await response.json();
}


/* CHECKOUT BUTTON */

const checkoutButton =
    document.getElementById(
        "checkout-button"
    );


checkoutButton.addEventListener(
    "click",
    async function () {

        const totalTickets =
            getMaximumSeats();


        if (totalTickets === 0) {

            alert(
                "Please select at least one ticket."
            );

            return;
        }


        if (
            selectedSeats.length !==
            totalTickets
        ) {

            alert(
                "Please select exactly " +
                totalTickets +
                " seat(s)."
            );

            return;
        }


        checkoutButton.disabled =
            true;


        try {

            const booking =
                await createBooking();


            await reserveSeats(
                booking.bookingId
            );


            sessionStorage.setItem(
                "bookingId",
                booking.bookingId
            );


            window.location.href =
                "checkout.html";


            /*
             * Next step:
             * Login / Order Summary
             */

        } catch (error) {

            console.error(
                "Booking error:",
                error
            );

            alert(
                error.message
            );

            await loadUnavailableSeats();

        } finally {

            checkoutButton.disabled =
                false;
        }
    }
);


/* INITIAL LOAD */

if (!showtimeId) {

    seatMessage.textContent =
        "No showtime was selected.";

} else {

    loadUnavailableSeats();
}


updateTicketDisplay();
updateSummary();