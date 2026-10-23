
async function loadProfile() {
    const message = document.getElementById("profileMessage");

    try {
        const response = await fetch("/api/profile", {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error("Could not load your profile.");
        }

        const user = await response.json();

        document.getElementById("firstName").value =
            user.firstName ?? "";

        document.getElementById("lastName").value =
            user.lastName ?? "";

        document.getElementById("username").value =
            user.username ?? "";

        document.getElementById("email").value =
            user.email ?? "";

        message.textContent = "";
    } catch (error) {
        message.textContent =
            "Unable to load your profile. Please try again.";
    }
}

document.addEventListener("DOMContentLoaded", loadProfile);
