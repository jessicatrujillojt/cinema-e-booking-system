const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {

    const loginForm = document.getElementById("loginForm");
    const loginMessage = document.getElementById("loginMessage");

    loginForm.addEventListener("submit", async (event) => {

        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        loginMessage.textContent = "Logging in...";

        try {
            const response = await fetch(
                `${API_BASE}/api/auth/login`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    credentials: "include",
                    body: JSON.stringify({
                        emailAddress: email,
                        password: password
                    })
                }
            );

            const result = await response.json();

            if (!response.ok || !result.success) {
                loginMessage.textContent =
                    result.message || "Invalid email or password.";
                return;
            }

            loginMessage.textContent = "Login successful!";

            // Redirect after successful login
            if (result.role === "ADMIN") {
                window.location.href = "admin.html";
            } else {
                window.location.href = "profile.html";
            }

        } catch (error) {
            console.error("Login error:", error);

            loginMessage.textContent =
                "Unable to connect to the server.";
        }
    });

    // Check whether the user is already logged in
    checkSession();
});

async function checkSession() {
    try {
        const response = await fetch(
            `${API_BASE}/api/auth/session`,
            { credentials: "include" }
        );

        if (!response.ok) return;

        const session = await response.json();

        const profileNav = document.getElementById("profileNav");
        const loginNav = document.getElementById("loginNav");
        const logoutNav = document.getElementById("logoutNav");

        if (session.loggedIn) {
            profileNav.hidden = false;
            loginNav.hidden = true;
            logoutNav.hidden = false;
        } else {
            profileNav.hidden = true;
            loginNav.hidden = false;
            logoutNav.hidden = true;
        }

    } catch (error) {
        console.error("Session error:", error);
    }
}

// Logout
document.addEventListener("click", async (event) => {

    if (event.target.id !== "logoutButton") return;

    try {
        const response = await fetch(
            `${API_BASE}/api/auth/logout`,
            {
                method: "POST",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error("Logout failed");
        }

        window.location.href = "home.html";

    } catch (error) {
        console.error("Logout error:", error);
    }
});