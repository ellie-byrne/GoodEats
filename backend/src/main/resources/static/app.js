// Restaurant Management Functions

// Variables to track the current restaurant being deleted
let currentDeleteId = null;

// Toggle Add Restaurant Modal
function toggleAddRestaurantModal() {
    const modal = document.getElementById("add-restaurant-modal");
    modal.style.display = modal.style.display === "block" ? "none" : "block";

    // Reset form on close
    if (modal.style.display === "none") {
        document.getElementById("add-restaurant-form").reset();
    }
}

// Toggle Delete Confirmation Modal
function toggleDeleteConfirmModal(restaurantId) {
    const modal = document.getElementById("delete-confirm-modal");

    if (modal.style.display !== "block") {
        // Opening the modal - store the restaurant ID
        currentDeleteId = restaurantId;
    } else {
        // Closing the modal - clear the restaurant ID
        currentDeleteId = null;
    }

    modal.style.display = modal.style.display === "block" ? "none" : "block";
}

// Add Restaurant
async function addRestaurant(event) {
    event.preventDefault();

    const nameInput = document.getElementById("restaurant-name");
    const typeInput = document.getElementById("restaurant-type");
    const boroughInput = document.getElementById("restaurant-borough");
    const photoInput = document.getElementById("restaurant-photo");

    const restaurantData = {
        name: nameInput.value,
        type: typeInput.value,
        Borough: boroughInput.value,
        storePhoto: photoInput.value || "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg"
    };

    try {
        const response = await fetch("http://localhost:8080/api/restaurants", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(restaurantData),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const newRestaurant = await response.json();

        // Add to allRestaurants array
        allRestaurants.push(newRestaurant);

        // Refresh the display
        displayRestaurants(allRestaurants);

        // Show success message
        showMessage(`Restaurant "${newRestaurant.name}" added successfully!`);

        // Close the modal
        toggleAddRestaurantModal();

    } catch (error) {
        console.error("Error adding restaurant:", error);
        showMessage(`Error adding restaurant: ${error.message}`, true);
    }
}

// Delete Restaurant
async function deleteRestaurant() {
    if (!currentDeleteId) return;

    try {
        const response = await fetch(`http://localhost:8080/api/restaurants/${currentDeleteId}`, {
            method: "DELETE",
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Remove from allRestaurants array
        const deletedRestaurantIndex = allRestaurants.findIndex(r => r.id === currentDeleteId);
        const deletedRestaurantName = allRestaurants[deletedRestaurantIndex].name;
        allRestaurants.splice(deletedRestaurantIndex, 1);

        // Refresh the display
        displayRestaurants(allRestaurants);

        // Show success message
        showMessage(`Restaurant "${deletedRestaurantName}" deleted successfully!`);

        // Close the modal
        toggleDeleteConfirmModal();

    } catch (error) {
        console.error("Error deleting restaurant:", error);
        showMessage(`Error deleting restaurant: ${error.message}`, true);
    }
}

// Show message function
function showMessage(message, isError = false) {
    const messageEl = document.createElement("div");
    messageEl.className = isError ? "error-message" : "success-message";
    messageEl.textContent = message;

    document.body.appendChild(messageEl);

    setTimeout(() => {
        messageEl.style.opacity = "0";
        setTimeout(() => {
            document.body.removeChild(messageEl);
        }, 500);
    }, 3000);
}

// Modified displayRestaurants function to include delete buttons for logged-in users
function displayRestaurants(restaurants) {
    const favourites = JSON.parse(localStorage.getItem("goodEatsFavourites")) || [];

    const favouriteIds = favourites.reduce((map, restaurant) => {
        const id = restaurant.id || restaurant._id;
        if (id) map[id] = true;
        return map;
    }, {});

    container.innerHTML = "";

    if (!restaurants || restaurants.length === 0) {
        container.innerHTML = '<div class="no-results">No restaurants found matching your filters.</div>';
        return;
    }

    restaurants.forEach((restaurant) => {
        const name = restaurant.Name || restaurant.name || "Unnamed Restaurant";
        const type = restaurant.Category || restaurant.type || "Restaurant";
        const borough = restaurant.Borough || restaurant.borough || "";

        const imageUrl =
            restaurant.storePhoto ||
            "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg";

        const restaurantId = restaurant.id || restaurant._id;
        const isFavourite = favouriteIds[restaurantId] || false;

        // Create the card element
        const card = document.createElement("div");
        card.className = "restaurant-card";

        card.innerHTML = `
            ${isLoggedIn ? `<button class="delete-button" onclick="event.stopPropagation(); toggleDeleteConfirmModal(${restaurantId})">Delete</button>` : ''}
            <img src="${imageUrl}" alt="${name}" class="restaurant-image" 
                onerror="this.src='https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg'">
            <div class="restaurant-info">
                <h2 class="restaurant-name">${name}</h2>
                <span class="restaurant-type">${type}</span>
                <p class="restaurant-borough">${borough}</p>
                
                <div class="restaurant-rating">
                    <div class="stars" data-rating="0">
                        <span class="star" data-value="1">★</span>
                        <span class="star" data-value="2">★</span>
                        <span class="star" data-value="3">★</span>
                        <span class="star" data-value="4">★</span>
                        <span class="star" data-value="5">★</span>
                    </div>
                    <span class="rating-count">(0 ratings)</span>
                </div>
                
                ${
            restaurant.link
                ? `<a href="${restaurant.link}" class="restaurant-link" target="_blank">Visit Website</a>`
                : ""
        }
                <button class="favourite-button ${isFavourite ? "active" : ""}" data-id="${restaurantId}">★</button>
            </div>
        `;

        const favouriteButton = card.querySelector(".favourite-button");
        favouriteButton.addEventListener("click", (e) => {
            e.stopPropagation(); // Prevent card click
            toggleFavourite(restaurant, favouriteButton);
        });

        // Add click event to the card for recently viewed
        card.addEventListener("click", (e) => {
            if (e.target.closest(".favourite-button") || e.target.closest(".star") || e.target.closest(".delete-button")) {
                return;
            }
            addToRecentlyViewed(restaurant);

            window.location.href = `http://localhost:8080/restaurant-detail.html?id=${restaurantId}`;
        });

        container.appendChild(card);
    });

    setupRatings();
}

// Add this to the document.addEventListener("DOMContentLoaded", () => { ... }) function

// User restaurant controls setup
function initializeRestaurantControls() {
    const userRestaurantControls = document.getElementById("user-restaurant-controls");
    const addRestaurantButton = document.getElementById("add-restaurant-button");
    const addRestaurantForm = document.getElementById("add-restaurant-form");
    const confirmDeleteButton = document.getElementById("confirm-delete-button");

    // Add event listeners for user controls
    addRestaurantButton.addEventListener("click", toggleAddRestaurantModal);
    addRestaurantForm.addEventListener("submit", addRestaurant);
    confirmDeleteButton.addEventListener("click", deleteRestaurant);

    // Function to update user restaurant controls based on login status
    function updateUserRestaurantControls() {
        if (isLoggedIn) {
            userRestaurantControls.style.display = "flex";
        } else {
            userRestaurantControls.style.display = "none";
        }

        // Refresh the restaurant display to update delete buttons
        if (allRestaurants) {
            displayRestaurants(allRestaurants);
        }
    }

    // Update the existing updateAuthDisplay function to also update restaurant controls
    const originalUpdateAuthDisplay = updateAuthDisplay;
    updateAuthDisplay = function() {
        originalUpdateAuthDisplay();
        updateUserRestaurantControls();
    };

    // Initial call to set correct visibility
    updateUserRestaurantControls();

    // Add keyboard shortcut to close modals
    document.addEventListener("keydown", (e) => {
        // Press Escape to close modals
        if (e.key === 'Escape') {
            document.getElementById("add-restaurant-modal").style.display = "none";
            document.getElementById("delete-confirm-modal").style.display = "none";
        }
    });
}

// Call this function at the end of your DOMContentLoaded event
initializeRestaurantControls();