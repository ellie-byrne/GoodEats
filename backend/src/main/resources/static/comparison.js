document.addEventListener("DOMContentLoaded", () => {
    const darkModeToggle = document.getElementById("dark-mode-toggle");
    const restaurantsContainer = document.getElementById("restaurants-container");
    const compareButton = document.getElementById("compare-button");
    const clearButton = document.getElementById("clear-comparison");
    const comparisonResults = document.getElementById("comparison-results");

    setupDarkMode(darkModeToggle);

    loadRestaurants();

    // Setup event listeners
    document.querySelectorAll(".add-restaurant-btn").forEach(btn => {
        btn.addEventListener("click", function() {
            const slot = this.dataset.slot;
            toggleRestaurantSelection(slot);
        });
    });

    clearButton.addEventListener("click", clearComparison);

    function loadRestaurants() {
        restaurantsContainer.innerHTML = '<div class="loading">Loading restaurants...</div>';

        fetch("http://localhost:8080/api/restaurants")
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(restaurants => {
                displayRestaurants(restaurants);
            })
            .catch(error => {
                console.error("Error fetching restaurants:", error);
                restaurantsContainer.innerHTML = `
                    <div class="error">
                        <p>Error loading restaurants. Please try again later.</p>
                        <p class="error-details">${error.message}</p>
                        <button onclick="loadRestaurants()">Retry</button>
                    </div>
                `;
            });
    }

    function displayRestaurants(restaurants) {
        if (!restaurants || restaurants.length === 0) {
            restaurantsContainer.innerHTML = '<div class="no-results">No restaurants found.</div>';
            return;
        }

        restaurantsContainer.innerHTML = '';

        restaurants.sort((a, b) => {
            const nameA = (a.name || a.Name || "").toLowerCase();
            const nameB = (b.name || b.Name || "").toLowerCase();
            return nameA.localeCompare(nameB);
        });

        restaurants.forEach(restaurant => {
            const name = restaurant.name || restaurant.Name || "Unnamed Restaurant";
            const type = restaurant.type || restaurant.Category || "Restaurant";
            const borough = restaurant.Borough ?? restaurant.borough ?? "";
            const id = restaurant.id || restaurant._id;

            const restaurantItem = document.createElement("div");
            restaurantItem.className = "restaurant-select-item";
            restaurantItem.dataset.id = id;
            restaurantItem.innerHTML = `
                <div class="restaurant-info">
                    <div class="restaurant-name">${name}</div>
                    <div class="restaurant-details">${type} • ${borough}</div>
                </div>
            `;

            restaurantItem.addEventListener("click", () => {
                if (document.querySelector(".restaurant-selection-active")) {
                    const slot = document.querySelector(".restaurant-selection-active").dataset.slot;
                    selectRestaurant(restaurant, slot);
                }
            });

            restaurantsContainer.appendChild(restaurantItem);
        });
    }

    function toggleRestaurantSelection(slot) {
        document.querySelectorAll(".add-restaurant-btn").forEach(btn => {
            btn.classList.remove("restaurant-selection-active");
        });

        const btn = document.querySelector(`.add-restaurant-btn[data-slot="${slot}"]`);
        if (btn) {
            btn.classList.add("restaurant-selection-active");
            btn.textContent = "Selecting...";

            document.querySelector(".restaurant-list").scrollIntoView({ behavior: "smooth" });
        }
    }

    function selectRestaurant(restaurant, slot) {
        document.querySelectorAll(".add-restaurant-btn").forEach(btn => {
            btn.classList.remove("restaurant-selection-active");
            btn.textContent = "+ Add Restaurant";
        });

        const selectionContainer = document.getElementById(`restaurant${slot}-selection`);
        if (!selectionContainer) return;

        const name = restaurant.name || restaurant.Name || "Unnamed Restaurant";
        const type = restaurant.type || restaurant.Category || "Restaurant";
        const borough = restaurant.Borough ?? restaurant.borough ?? "";
        const id = restaurant.id || restaurant._id;
        let imageUrl = restaurant.storePhoto || "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg";

        selectionContainer.innerHTML = `
            <div class="selected-restaurant-card" data-id="${id}">
                <img src="${imageUrl}" alt="${name}" onerror="this.src='https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg'">
                <div class="info">
                    <h4>${name}</h4>
                    <p>${type} • ${borough}</p>
                </div>
                <button class="remove-btn" data-slot="${slot}">&times;</button>
            </div>
        `;

        // Add event listener to remove button
        const removeBtn = selectionContainer.querySelector(".remove-btn");
        if (removeBtn) {
            removeBtn.addEventListener("click", function(e) {
                e.stopPropagation();
                removeRestaurant(this.dataset.slot);
            });
        }

        checkCompareButton();
    }

    function removeRestaurant(slot) {
        const selectionContainer = document.getElementById(`restaurant${slot}-selection`);
        if (!selectionContainer) return;

        selectionContainer.innerHTML = `<button class="add-restaurant-btn" data-slot="${slot}">+ Add Restaurant</button>`;

        const newBtn = selectionContainer.querySelector(".add-restaurant-btn");
        if (newBtn) {
            newBtn.addEventListener("click", function() {
                const slot = this.dataset.slot;
                toggleRestaurantSelection(slot);
            });
        }

        compareButton.disabled = true;

        comparisonResults.classList.add("hidden");
    }

    function clearComparison() {
        removeRestaurant(1);
        removeRestaurant(2);
        comparisonResults.classList.add("hidden");
    }

    function checkCompareButton() {
        const restaurant1 = document.querySelector("#restaurant1-selection .selected-restaurant-card");
        const restaurant2 = document.querySelector("#restaurant2-selection .selected-restaurant-card");

        compareButton.disabled = !(restaurant1 && restaurant2);
    }

    function setupDarkMode(toggle) {
        const enabled = localStorage.getItem("darkModeEnabled") === "true";
        if (enabled) {
            document.body.classList.add("dark-mode");
            toggle.checked = true;
        }

        toggle.addEventListener("change", () => {
            document.body.classList.toggle("dark-mode", toggle.checked);
            localStorage.setItem("darkModeEnabled", toggle.checked ? "true" : "false");
        });
    }
});