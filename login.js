const API_BASE = "http://localhost:8080";

function showMessage(message, type) {

    const loginMessage =
        document.getElementById("loginMessage");

    loginMessage.textContent = message;
    loginMessage.className = "message-box " + type;
    loginMessage.hidden = false;

}
document.addEventListener(
    "DOMContentLoaded",
    function () {

        const loginForm =
            document.getElementById("loginForm");

        const loginMessage =
            document.getElementById("loginMessage");


        loginForm.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();


                const emailAddress =
                    document
                        .getElementById("email")
                        .value
                        .trim();

                const password =
                    document
                        .getElementById("password")
                        .value;


                showMessage("Logging in...", "info");


                try {

                    const response = await fetch(
                        `${API_BASE}/api/auth/login`,
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            credentials: "include",

                            body: JSON.stringify({
                                emailAddress,
                                password
                            })
                        }
                    );


                    const result =
                        await response.json();


                    if (!response.ok) {

                        showMessage(
                            result.message ||
                            "Invalid email or password.", "error"
                        );

                        return;
                    }


                    showMessage(
                        "Login successful!", "success"
                    );


                    const returnToCheckout =
                            sessionStorage.getItem(
                                "returnToCheckout"
                            );

                        if (
                            returnToCheckout === "true" &&
                            result.role !== "ADMIN"
                        ) {

                            sessionStorage.removeItem(
                                "returnToCheckout"
                            );

                            window.location.href =
                                "checkout.html";

                        } else if (
                            result.role === "ADMIN"
                        ) {

                            window.location.href =
                                "adminhome.html";

                        } else {

                            window.location.href =
                                "profile.html";
                        }
                } catch (error) {

                    console.error(
                        "Login error:",
                        error
                    );

                    showMessage(
                        "Unable to connect to the server.", "error"
                    );
                }
            }
        );
    }
);