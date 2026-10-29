const API_BASE = "http://localhost:8080";


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


                loginMessage.textContent =
                    "Logging in...";


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

                        loginMessage.textContent =
                            result.message ||
                            "Invalid email or password.";

                        return;
                    }


                    loginMessage.textContent =
                        "Login successful!";


                    if (result.role === "ADMIN") {

                        window.location.href =
                            "adminhome.html";

                    } else {

                        window.location.href =
                            "home.html";
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
    }
);