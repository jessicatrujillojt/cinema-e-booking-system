const API_BASE =
    "http://localhost:8080";


const promotionForm =
    document.getElementById(
        "cardForm"
    );

const promotionsList =
    document.getElementById(
        "promotions-list"
    );


async function loadPromotions() {

    try {

        const response = await fetch(
            `${API_BASE}/api/promotions`,
            {
                method: "GET",
                credentials: "include"
            }
        );


        if (!response.ok) {
            throw new Error(
                "Failed to load promotions."
            );
        }


        const promotions =
            await response.json();


        promotionsList.innerHTML = "";


        promotions.forEach(function (promotion) {

            const item =
                document.createElement("li");


            item.textContent =
                `${promotion.title} - ` +
                `${promotion.discount}% off - ` +
                `Expires ${promotion.expiryDate}`;


            promotionsList.appendChild(
                item
            );
        });


        if (promotions.length === 0) {

            const item =
                document.createElement("li");

            item.textContent =
                "No current promotions.";

            promotionsList.appendChild(
                item
            );
        }


    } catch (error) {

        console.error(
            "Load promotions error:",
            error
        );
    }
}



promotionForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const promotion = {

            title:
                document
                    .getElementById("Title")
                    .value
                    .trim(),

            description:
                document
                    .getElementById("Description")
                    .value
                    .trim(),

            discount:
                Number(
                    document
                        .getElementById("Discount")
                        .value
                ),

            expiryDate:
                document
                    .getElementById("ExpiryDate")
                    .value
        };


        try {

            const response = await fetch(
                `${API_BASE}/api/promotions`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    credentials: "include",

                    body:
                        JSON.stringify(
                            promotion
                        )
                }
            );


            const result =
                await response.json();


            if (!response.ok) {

                alert(
                    result.error ||
                    "Unable to save promotion."
                );

                return;
            }


            alert(
                `Promotion "${result.title}" added successfully.`
            );


            promotionForm.reset();


            await loadPromotions();


        } catch (error) {

            console.error(
                "Add promotion error:",
                error
            );

            alert(
                "Unable to add promotion."
            );
        }
    }
);



document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadPromotions();

    }
);