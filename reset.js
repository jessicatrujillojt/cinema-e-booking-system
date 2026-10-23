const resetForm =
    document.getElementById("reset-form");

const resetMessage =
    document.getElementById("reset-message");

const urlParameters =
    new URLSearchParams(window.location.search);

const token =
    urlParameters.get("token");

resetForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const password =
            document.getElementById(
                "password"
            ).value;

        const confirmPassword =
            document.getElementById(
                "confirm-password"
            ).value;

        if (!token) {

            resetMessage.textContent =
                "Invalid or missing password reset token.";

            return;
        }

        if (password !== confirmPassword) {

            resetMessage.textContent =
                "Passwords do not match.";

            return;
        }

        try {

            const response = await fetch(
                "http://localhost:8080/api/auth/reset-password",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        token: token,
                        newPassword: password
                    })
                }
            );

            const data =
                await response.json();

            if (response.ok) {

                resetMessage.textContent =
                    data.message;

                setTimeout(function () {

                    window.location.href =
                        "login.html";

                }, 1500);

            } else {

                resetMessage.textContent =
                    data.message ||
                    "Password reset failed.";
            }

        } catch (error) {

            console.error(
                "Reset password error:",
                error
            );

            resetMessage.textContent =
                "Unable to connect to the server.";
        }
    }
);