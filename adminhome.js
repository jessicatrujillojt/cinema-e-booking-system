const API_BASE = "http://localhost:8080";


async function checkAdminAccess() {

    try {

        const response = await fetch(
            `${API_BASE}/api/admin/check`,
            {
                method: "GET",
                credentials: "include"
            }
        );

        if (!response.ok) {

            window.location.href =
                "login.html";
        }

    } catch (error) {

        console.error(
            "Admin access check error:",
            error
        );

        window.location.href =
            "login.html";
    }
}


async function logout() {

    try {

        await fetch(
            `${API_BASE}/api/auth/logout`,
            {
                method: "POST",
                credentials: "include"
            }
        );

        window.location.href =
            "login.html";

    } catch (error) {

        console.error(
            "Logout error:",
            error
        );
    }
}


document.addEventListener(
    "DOMContentLoaded",
    function () {

        checkAdminAccess();

        document
            .getElementById("logout-button")
            .addEventListener(
                "click",
                logout
            );
    }
);