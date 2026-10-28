const API_BASE = "http://localhost:8080";

async function loadProfile() {
    const message = document.getElementById("profileMessage");

    try {
        // Check login session
        const sessionResponse = await fetch(
            `${API_BASE}/api/auth/session`,
            { credentials: "include" }
        );

        if (!sessionResponse.ok) {
            throw new Error("Could not check login session.");
        }

        const session = await sessionResponse.json();

        if (!session.loggedIn) {
            window.location.href = "login.html";
            return;
        }

        // Retrieve profile information
        const response = await fetch(
            `${API_BASE}/api/profile`,
            { credentials: "include" }
        );

        if (!response.ok) {
            throw new Error("Could not load profile.");
        }

        const user = await response.json();

        // Display welcome message on profile.html
        const welcomeMessage =
            document.getElementById("welcomeMessage");

        if (welcomeMessage) {
            welcomeMessage.textContent =
                `Welcome, ${user.firstName}!`;
        }

        // Populate edit-profile.html fields
        const fields = [
            "firstName",
            "lastName",
            "email",
            "phoneNumber",
            "address",
            "city",
            "state",
            "zipCode"
        ];

        fields.forEach(field => {
            const input = document.getElementById(field);

            if (input) {
                input.value = user[field] ?? "";
            }
        });

        const promotions =
            document.getElementById("subscribedToPromotions");

        if (promotions) {
            promotions.checked =
                user.subscribedToPromotions ?? false;
        }

        if (message) {
            message.textContent = "";
        }

    } catch (error) {
        console.error("Profile error:", error);

        if (message) {
            message.textContent =
                "Unable to load your profile.";
        }
    }
}

// Save profile changes
async function saveProfile(event) {
    event.preventDefault();

    const message = document.getElementById("profileMessage");

    const updatedProfile = {
        firstName: document.getElementById("firstName").value.trim(),
        lastName: document.getElementById("lastName").value.trim(),
        phoneNumber: document.getElementById("phoneNumber").value.trim(),
        address: document.getElementById("address").value.trim(),
        city: document.getElementById("city").value.trim(),
        state: document.getElementById("state").value.trim(),
        zipCode: document.getElementById("zipCode").value.trim(),
        subscribedToPromotions:
            document.getElementById("subscribedToPromotions").checked
    };

    message.textContent = "Saving changes...";

    try {
        const response = await fetch(
            `${API_BASE}/api/profile`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                credentials: "include",
                body: JSON.stringify(updatedProfile)
            }
        );

        if (!response.ok) {
            throw new Error("Could not save profile.");
        }

        message.textContent =
            "Profile updated successfully!";

    } catch (error) {
        console.error("Save profile error:", error);

        message.textContent =
            "Unable to save your changes. Please try again.";
    }
}

document.addEventListener("DOMContentLoaded", () => {

    loadProfile();

    const form = document.getElementById("editProfileForm");

    if (form) {
        form.addEventListener("submit", saveProfile);
    }
});