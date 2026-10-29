const changePasswordForm =
    document.getElementById("change-password-form");

const changePasswordMessage =
    document.getElementById("change-password-message");

changePasswordForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const currentPassword =
            document.getElementById(
                "currentPassword"
            ).value;

        const newPassword =
            document.getElementById(
                "newPassword"
            ).value;

        const confirmPassword =
            document.getElementById(
                "confirmPassword"
            ).value;

        if (newPassword !== confirmPassword) {

            changePasswordMessage.textContent =
                "New passwords do not match.";

            return;
        }

        try {

            const response = await fetch(
                "http://localhost:8080/api/auth/change-password",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    credentials: "include",

                    body: JSON.stringify({
                        currentPassword:
                            currentPassword,

                        newPassword:
                            newPassword
                    })
                }
            );

            const data =
                await response.json();

            if (response.ok) {

                changePasswordMessage.textContent =
                    data.message;

                changePasswordForm.reset();

            } else {

                changePasswordMessage.textContent =
                    data.message ||
                    "Unable to change password.";
            }

        } catch (error) {

            console.error(
                "Change password error:",
                error
            );

            changePasswordMessage.textContent =
                "Unable to connect to the server.";
        }
    }
);