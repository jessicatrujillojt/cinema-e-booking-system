const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {

    const registerForm = document.getElementById("registerForm");
    const registerMessage = document.getElementById("registerMessage");

    registerForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        const firstName =
            document.getElementById("firstName").value.trim();

        const lastName =
            document.getElementById("lastName").value.trim();

        const emailAddress =
            document.getElementById("email").value.trim();

        const phoneNumber =
            document.getElementById("phoneNumber").value.trim();

        const password =
            document.getElementById("password").value;

        const promotionSubscriptions =
            document.getElementById("promotionSubscriptions").checked;

        registerMessage.textContent = "Creating account...";

        try {
            const response = await fetch(
                `${API_BASE}/api/auth/register`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    credentials: "include",
                    body: JSON.stringify({
                        firstName,
                        lastName,
                        emailAddress,
                        phoneNumber,
                        password,
                        promotionSubscriptions
                    })
                }
            );

            const result = await response.json();

            if (!response.ok || !result.success) {
                registerMessage.textContent =
                    result.message || "Registration failed.";
                return;
            }

            registerMessage.textContent =
                "Account created successfully! Please verify your account before logging in.";

            registerForm.reset();

        } catch (error) {
            console.error("Registration error:", error);

            registerMessage.textContent =
                "Unable to connect to the server.";
        }
    });
});