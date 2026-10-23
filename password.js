const forgotPasswordForm =
    document.getElementById("forgot-password-form");

const forgotPasswordMessage =
    document.getElementById("forgot-password-message");


forgotPasswordForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const emailAddress =
            document.getElementById("email").value;

        try {

            const response = await fetch(
                "http://localhost:8080/api/auth/forgot-password",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        emailAddress: emailAddress
                    })
                }
            );

            const data = await response.json();

            if (response.ok) {

                forgotPasswordMessage.textContent =
                    "Password reset email sent. Please check your email.";

                forgotPasswordForm.reset();

            } else {

                forgotPasswordMessage.textContent =
                    data.message ||
                    "Unable to send password reset email.";
            }

        } catch (error) {

            console.error(
                "Forgot password error:",
                error
            );

            forgotPasswordMessage.textContent =
                "Unable to connect to the server.";
        }
    }
);