const API_BASE = "http://localhost:8080";

const paymentCards = document.getElementById("paymentCards");
const paymentMessage = document.getElementById("paymentMessage");
const addCardButton = document.getElementById("addCardButton");
const cardFormSection = document.getElementById("cardFormSection");
const cardForm = document.getElementById("cardForm");
const cardFormTitle = document.getElementById("cardFormTitle");
const saveCardButton = document.getElementById("saveCardButton");

let editingCardId = null;
let savedCards = [];

// Request helper
async function cardRequest(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        }
    });

    if (response.status === 401) {
        window.location.href = "login.html";
        throw new Error("Please log in.");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Payment request failed.");
    }

    if (response.status === 204) return null;

    return response.json();
}

// Check login session
async function checkLogin() {
    const session = await cardRequest("/api/auth/session");

    if (!session.loggedIn) {
        window.location.href = "login.html";
        return false;
    }

    return true;
}

// Load saved cards
async function loadCards() {
    try {
        savedCards = await cardRequest("/api/payment-cards");
        displayCards();
    } catch (error) {
        paymentMessage.textContent = error.message;
    }
}

// Display saved cards
function displayCards() {
    paymentCards.innerHTML = "";

    savedCards.forEach((card, index) => {
        const container = document.createElement("div");
        container.classList.add("card-container");

        const section = document.createElement("section");
        section.classList.add("payment-info");

        const heading = document.createElement("h3");
        heading.textContent = `Payment Method #${index + 1}`;

        const number = document.createElement("p");
        number.textContent = `Card Number: **** **** **** ${card.lastFour}`;

        const expiration = document.createElement("p");
        expiration.textContent =
            `Expiration Date: ${String(card.expirationMonth).padStart(2, "0")}/${card.expirationYear}`;

        const name = document.createElement("p");
        name.textContent = `Cardholder Name: ${card.cardholderName}`;

        const actions = document.createElement("div");
        actions.classList.add("card-actions");

        const editButton = document.createElement("button");
        editButton.type = "button";
        editButton.classList.add("edit-btn");
        editButton.textContent = "Edit";
        editButton.addEventListener("click", () => editCard(card));

        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.classList.add("delete-btn");
        deleteButton.textContent = "Delete";
        deleteButton.addEventListener("click", () => deleteCard(card.id));

        actions.append(editButton, deleteButton);
        section.append(heading, number, expiration, name, actions);
        container.appendChild(section);
        paymentCards.appendChild(container);
    });

    if (savedCards.length === 0) {
        const empty = document.createElement("p");
        empty.textContent = "You have no saved payment methods.";
        paymentCards.appendChild(empty);
    }

    // Maximum 3 saved cards
    addCardButton.disabled = savedCards.length >= 3;
    addCardButton.textContent = savedCards.length >= 3
        ? "Maximum 3 Payment Methods"
        : "Add Payment Method";
}

// Open form to add a card
addCardButton.addEventListener("click", () => {
    if (savedCards.length >= 3) return;

    editingCardId = null;
    cardForm.reset();

    cardFormTitle.textContent = "Add Payment Method";
    saveCardButton.textContent = "Save Card";

    cardFormSection.hidden = false;
});

// Open form to edit a card
function editCard(card) {
    editingCardId = card.id;

    cardForm.reset();

    document.getElementById("cardholderName").value =
        card.cardholderName;

    document.getElementById("expirationMonth").value =
        card.expirationMonth;

    document.getElementById("expirationYear").value =
        card.expirationYear;

    // Full card numbers are never returned by the API
    document.getElementById("cardNumber").value = "";

    cardFormTitle.textContent =
        `Edit Payment Method ending in ${card.lastFour}`;

    saveCardButton.textContent = "Update Card";
    cardFormSection.hidden = false;
}

// Save a new or edited card
cardForm.addEventListener("submit", async event => {
    event.preventDefault();

    const request = {
        cardholderName:
            document.getElementById("cardholderName").value.trim(),

        cardNumber:
            document.getElementById("cardNumber").value.trim(),

        expirationMonth:
            Number(document.getElementById("expirationMonth").value),

        expirationYear:
            Number(document.getElementById("expirationYear").value)
    };

    saveCardButton.disabled = true;
    paymentMessage.textContent = "Saving payment method...";

    try {
        const isEditing = editingCardId !== null;

        await cardRequest(
            isEditing
                ? `/api/payment-cards/${editingCardId}`
                : "/api/payment-cards",
            {
                method: isEditing ? "PUT" : "POST",
                body: JSON.stringify(request)
            }
        );

        paymentMessage.textContent =
            "Payment method saved successfully!";

        cardForm.reset();
        cardFormSection.hidden = true;
        editingCardId = null;

        await loadCards();

    } catch (error) {
        paymentMessage.textContent = error.message;
    } finally {
        saveCardButton.disabled = false;
    }
});

// Delete a saved card
async function deleteCard(cardId) {
    if (!confirm("Are you sure you want to delete this payment method?")) {
        return;
    }

    try {
        await cardRequest(`/api/payment-cards/${cardId}`, {
            method: "DELETE"
        });

        paymentMessage.textContent = "Payment method deleted.";
        await loadCards();

    } catch (error) {
        paymentMessage.textContent = error.message;
    }
}

// Cancel editing
document.getElementById("cancelCardButton")
    .addEventListener("click", () => {
        cardForm.reset();
        editingCardId = null;
        cardFormSection.hidden = true;
    });

// Initialize page
document.addEventListener("DOMContentLoaded", async () => {
    try {
        if (await checkLogin()) {
            await loadCards();
        }
    } catch (error) {
        paymentMessage.textContent = error.message;
    }
});