const params = new URLSearchParams(window.location.search);

const movie = params.get("movie");
const date = params.get("date");
const time = params.get("time");

if (movie) {
    document.getElementById("booking-movie-title").textContent = movie;
}

if (date) {
    document.getElementById("booking-date").textContent = date;
}

if (time) {
    document.getElementById("booking-time").textContent = time;
}
const prices = {
    adult: 10.00,
    child: 5.00,
    senior: 9.00
};

const quantities = {
    adult: 0,
    child: 0,
    senior: 0
};

const selectedSeats = [];
const seatMessage = document.getElementById("seat-message");

/*TICKET QUANTITY*/

const quantityButtons =
    document.querySelectorAll(".quantity-button");

quantityButtons.forEach(function(button) {

    button.addEventListener("click", function() {

        const type = button.dataset.type;
        const action = button.dataset.action;

        if (action === "plus") {

            quantities[type]++;

        } else if (action === "minus") {

            if (quantities[type] > 0) {
                quantities[type]--;
            }

        }

        updateTicketDisplay();
        updateSummary();

    });

});

function getMaximumSeats() {

    return (
        quantities.adult +
        quantities.child +
        quantities.senior
    );

}

function updateTicketDisplay() {

    document.getElementById("adult-quantity").textContent =
        quantities.adult;

    document.getElementById("child-quantity").textContent =
        quantities.child;

    document.getElementById("senior-quantity").textContent =
        quantities.senior;
}


/*SEAT SELECTION*/

const seats =
    document.querySelectorAll(".seat");

seats.forEach(function(seat) {

    seat.addEventListener("click", function() {
        if (seat.classList.contains("taken")) {
            return;
        }

        const seatName = seat.dataset.seat;

        if (selectedSeats.includes(seatName)) {

            const index =
                selectedSeats.indexOf(seatName);

            selectedSeats.splice(index, 1);

            seat.classList.remove("selected");
            seatMessage.textContent = "";

        } else {
            const maxSeats = getMaximumSeats();
            if (selectedSeats.length >= maxSeats) {
                seatMessage.textContent = "Maximum number of seats reached. You can only select " + maxSeats + " seats. ";
                return;
            }

            selectedSeats.push(seatName);

            seat.classList.add("selected");
            seatMessage.textContent = "";
        }

        updateSummary();

    });

});


/*SUMMARY*/

function updateSummary() {

    const selectedSeatsElement =
        document.getElementById("selected-seats");

    const totalPriceElement =
        document.getElementById("total-price");


    /* Selected seats */

    if (selectedSeats.length === 0) {

        selectedSeatsElement.textContent = "None";

    } else {

        selectedSeatsElement.textContent =
            selectedSeats.join(", ");

    }


    /* Calculate total */

    const total =
        (quantities.adult * prices.adult) +
        (quantities.child * prices.child) +
        (quantities.senior * prices.senior);

    totalPriceElement.textContent =
        "$" + total.toFixed(2);
}


/*CHECKOUT BUTTON*/

const checkoutButton =
    document.getElementById("checkout-button");

checkoutButton.addEventListener("click", function() {

    alert(
        "Checkout functionality will be implemented in a later sprint."
    );

});
