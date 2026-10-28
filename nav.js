
const NAV_API_BASE = "http://localhost:8080";

// Check the logged-in user's session
async function updateNavigation() {

    const profileNav = document.getElementById("profileNav");
    const loginNav = document.getElementById("loginNav");
    const logoutNav = document.getElementById("logoutNav");

    try {
        const response = await fetch(
            `${NAV_API_BASE}/api/auth/session`,
            {
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error("Unable to check session");
        }

        const session = await response.json();
        const loggedIn = session.loggedIn === true;

        // Show Profile only when logged in
        if (profileNav) {
            profileNav.hidden = !loggedIn;
        }

        // Show Login only when logged out
        if (loginNav) {
            loginNav.hidden = loggedIn;
        }

        // Show Logout only when logged in
        if (logoutNav) {
            logoutNav.hidden = !loggedIn;
        }

    } catch (error) {
        console.error("Navigation error:", error);

        if (profileNav) profileNav.hidden = true;
        if (logoutNav) logoutNav.hidden = true;
        if (loginNav) loginNav.hidden = false;
    }
}

// Log out the current user
async function logout() {

    try {
        const response = await fetch(
            `${NAV_API_BASE}/api/auth/logout`,
            {
                method: "POST",
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error("Logout failed");
        }

        // Return to homepage after logout
        window.location.href = "home.html";

    } catch (error) {
        console.error("Logout error:", error);
        alert("Unable to log out. Please try again.");
    }
}

// Run when the page loads
document.addEventListener("DOMContentLoaded", () => {

    updateNavigation();

    const logoutButton =
        document.getElementById("logoutButton");

    if (logoutButton) {
        logoutButton.addEventListener("click", logout);
    }
});
