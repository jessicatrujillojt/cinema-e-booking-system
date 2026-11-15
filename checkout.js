const API_BASE =
    "http://localhost:8080";


const bookingId =
    sessionStorage.getItem(
        "bookingId"
    );


const checkoutMessage =
    document.getElementById(
        "checkout-message"
    );


async function loadSummary() {

    if (!bookingId) {

        checkoutMessage.textContent =
            "No active booking was found.";

        return;
    }


    try {

        const response = await fetch(
            `${API_BASE}/api/bookings/${bookingId}/summary`,
            {
                method: "GET",
                credentials: "include"
            }
        );


        if (response.status === 401) {

            sessionStorage.setItem(
                "returnToCheckout",
                "true"
            );

            window.location.href =
                "login.html";

            return;
        }


        if (!response.ok) {

            const error =
                await response.json();

            throw new Error(
                error.error ||
                "Unable to load order summary."
            );
        }


        const summary =
            await response.json();


        document
            .getElementById(
                "summary-movie"
            )
            .textContent =
            summary.movieTitle;


        document
            .getElementById(
                "summary-date"
            )
            .textContent =
            summary.showDate;


        document
            .getElementById(
                "summary-time"
            )
            .textContent =
            formatTime(
                summary.showTime
            );


        document
            .getElementById(
                "summary-showroom"
            )
            .textContent =
            summary.showroomName;


        document
            .getElementById(
                "summary-seats"
            )
            .textContent =
            summary.selectedSeats.join(
                ", "
            );


        document
            .getElementById(
                "summary-adult"
            )
            .textContent =
            summary.adultTickets;


        document
            .getElementById(
                "summary-child"
            )
            .textContent =
            summary.childTickets;


        document
            .getElementById(
                "summary-senior"
            )
            .textContent =
            summary.seniorTickets;


        document
            .getElementById(
                "summary-total-tickets"
            )
            .textContent =
            summary.totalTickets;


        document
            .getElementById(
                "summary-adult-price"
            )
            .textContent =
            "$" +
            Number(
                summary.adultTicketPrice
            ).toFixed(2);


        document
            .getElementById(
                "summary-child-price"
            )
            .textContent =
            "$" +
            Number(
                summary.childTicketPrice
            ).toFixed(2);


        document
            .getElementById(
                "summary-senior-price"
            )
            .textContent =
            "$" +
            Number(
                summary.seniorTicketPrice
            ).toFixed(2);


        document
            .getElementById(
                "summary-total"
            )
            .textContent =
            "$" +
            Number(
                summary.totalBeforeTax
            ).toFixed(2);


        document
            .getElementById(
                "checkout-email"
            )
            .value =
            summary.emailAddress;


    } catch (error) {

        console.error(
            "Checkout summary error:",
            error
        );

        checkoutMessage.textContent =
            error.message;
    }
}


document
    .getElementById(
        "checkout-email-form"
    )
    .addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            const emailAddress =
                document
                    .getElementById(
                        "checkout-email"
                    )
                    .value
                    .trim();


            try {

                const response =
                    await fetch(
                        `${API_BASE}/api/bookings/${bookingId}/checkout-email`,
                        {
                            method: "PUT",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            credentials:
                                "include",

                            body:
                                JSON.stringify({
                                    emailAddress:
                                        emailAddress
                                })
                        }
                    );


                if (!response.ok) {

                    const error =
                        await response.json();

                    throw new Error(
                        error.error ||
                        "Unable to save checkout email."
                    );
                }


                await response.json();


                window.location.href =
                    "payment-page.html";


            } catch (error) {

                console.error(
                    "Checkout email error:",
                    error
                );

                checkoutMessage.textContent =
                    error.message;
            }
        }
    );


function formatTime(time) {

    const parts =
        time.split(":");

    let hour =
        Number(parts[0]);

    const minute =
        parts[1];


    const period =
        hour >= 12
            ? "PM"
            : "AM";


    hour =
        hour % 12;


    if (hour === 0) {
        hour = 12;
    }


    return (
        `${hour}:${minute} ${period}`
    );
}


loadSummary();