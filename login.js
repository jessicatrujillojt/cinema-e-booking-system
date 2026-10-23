const loginForm =
    document.getElementById("login-form");

const loginMessage =
    document.getElementById("login-message");

loginForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const emailAddress =
            document.getElementById(
                "emailAddress"
            ).value;

        const password =
            document.getElementById(
                "password"
            ).value;

        const loginData = {
            emailAddress: emailAddress,
            password: password
        };

        try {

            const response = await fetch(
                "http://localhost:8080/api/auth/login",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    credentials: "include",

                    body: JSON.stringify(
                        loginData
                    )
                }
            );

            const data =
                await response.json();

            if (response.ok) {

                loginMessage.textContent =
                    data.message;

                if (data.role === "ADMIN") {

                    window.location.href =
                        "adminhome.html";

                } else {

                    window.location.href =
                        "home.html";
                }

            } else {

                loginMessage.textContent =
                    data.message ||
                    "Login failed.";
            }

        } catch (error) {

            console.error(
                "Login error:",
                error
            );

            loginMessage.textContent =
                "Unable to connect to the server.";
        }
    }
);