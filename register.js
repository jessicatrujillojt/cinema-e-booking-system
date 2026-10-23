const registerForm =
    document.getElementById("register-form");

const registerMessage =
    document.getElementById("register-message");

registerForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const firstName =
            document.getElementById("firstName").value;

        const lastName =
            document.getElementById("lastName").value;

        const emailAddress =
            document.getElementById("email").value;

        const phoneNumber =
            document.getElementById("phoneNumber").value;

        const password =
            document.getElementById("password").value;

        const promotionSubscriptions =
            document.getElementById(
                "promotionSubscriptions"
            ).checked;

        const registrationData = {
            firstName: firstName,
            lastName: lastName,
            emailAddress: emailAddress,
            phoneNumber: phoneNumber,
            password: password,
            promotionSubscriptions: promotionSubscriptions
        };

        try {

            const response = await fetch(
                "http://localhost:8080/api/auth/register",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    credentials: "include",

                    body: JSON.stringify(
                        registrationData
                    )
                }
            );

            const data =
                await response.json();

            if (response.ok) {

                registerMessage.textContent =
                    data.message;

                registerForm.reset();

            } else {

                registerMessage.textContent =
                    data.message ||
                    "Registration failed.";
            }

        } catch (error) {

            console.error(
                "Registration error:",
                error
            );

            registerMessage.textContent =
                "Unable to connect to the server.";
        }
    }
);