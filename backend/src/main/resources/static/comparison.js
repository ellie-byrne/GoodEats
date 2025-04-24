document.addEventListener("DOMContentLoaded", () => {
    const darkModeToggle = document.getElementById("dark-mode-toggle");
    const restaurantsContainer = document.getElementById("restaurants-container");
    const compareButton = document.getElementById("compare-button");
    const clearButton = document.getElementById("clear-comparison");
    const comparisonResults = document.getElementById("comparison-results");

    setupDarkMode(darkModeToggle);

    loadRestaurants();

    setupEventListeners();

    function setupEventListeners() {
        document.querySelectorAll(".add-restaurant-btn").forEach(btn => {
            btn.addEventListener("click", function() {
                const slot = this.dataset.slot;
                toggleRestaurantSelection(slot);
            });
        });

        clearButton.addEventListener("click", clearComparison);
        compareButton.addEventListener("click", compareRestaurants);
    }

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

        const removeBtn = selectionContainer.querySelector(".remove-btn");
        if (removeBtn) {
            removeBtn.addEventListener("click", function(e) {
                e.stopPropagation();
                removeRestaurant(this.dataset.slot);
            });
        }

        checkCompareButton();

        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
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

        if (restaurant1 && restaurant2) {
            compareButton.disabled = false;
        } else {
            compareButton.disabled = true;
        }
    }

    function compareRestaurants() {
        const restaurant1El = document.querySelector("#restaurant1-selection .selected-restaurant-card");
        const restaurant2El = document.querySelector("#restaurant2-selection .selected-restaurant-card");

        if (!restaurant1El || !restaurant2El) {
            alert("Please select two restaurants to compare.");
            return;
        }

        const id1 = restaurant1El.dataset.id;
        const id2 = restaurant2El.dataset.id;

        Promise.all([
            fetch(`http://localhost:8080/api/restaurants/${id1}/reviews`).then(res => res.json()),
            fetch(`http://localhost:8080/api/restaurants/${id2}/reviews`).then(res => res.json())
        ])
            .then(([reviews1, reviews2]) => {
                // Calculate average ratings
                const avgRating1 = calculateAverageRating(reviews1);
                const avgRating2 = calculateAverageRating(reviews2);

                // Get restaurant names
                const name1 = restaurant1El.querySelector("h4").textContent;
                const name2 = restaurant2El.querySelector("h4").textContent;

                // Compare and display results
                displayComparisonResults(name1, name2, reviews1, reviews2, avgRating1, avgRating2);
            })
            .catch(error => {
                console.error("Error fetching reviews:", error);
                comparisonResults.innerHTML = `
                <div class="error">
                    <p>Error comparing restaurants. Please try again later.</p>
                    <p class="error-details">${error.message}</p>
                </div>
            `;
                comparisonResults.classList.remove("hidden");
            });
    }

    function calculateAverageRating(reviews) {
        if (!reviews || reviews.length === 0) return 0;

        const total = reviews.reduce((sum, review) => sum + (review.rating || 0), 0);
        return reviews.length ? parseFloat((total / reviews.length).toFixed(1)) : 0;
    }

    function displayComparisonResults(name1, name2, reviews1, reviews2, avgRating1, avgRating2) {
        let winner = null;
        let winnerName = null;
        let loserName = null;

        if (avgRating1 > avgRating2) {
            winner = 1;
            winnerName = name1;
            loserName = name2;
        } else if (avgRating2 > avgRating1) {
            winner = 2;
            winnerName = name2;
            loserName = name1;
        }

        let resultsHTML = `
            <h3>Comparison Results</h3>
            <div class="comparison-stats">
                <div class="stat-row">
                    <div class="stat-label">Restaurant:</div>
                    <div class="stat-value">${name1}</div>
                    <div class="stat-value">${name2}</div>
                </div>
                <div class="stat-row">
                    <div class="stat-label">Average Rating:</div>
                    <div class="stat-value">
                        ${avgRating1} / 5 stars
                        <div class="stars static-stars">
                            ${generateStarsHTML(avgRating1)}
                        </div>
                    </div>
                    <div class="stat-value">
                        ${avgRating2} / 5 stars
                        <div class="stars static-stars">
                            ${generateStarsHTML(avgRating2)}
                        </div>
                    </div>
                </div>
                <div class="stat-row">
                    <div class="stat-label">Number of Reviews:</div>
                    <div class="stat-value">${reviews1.length}</div>
                    <div class="stat-value">${reviews2.length}</div>
                </div>
                <div class="stat-row">
                    <div class="stat-label">5★ Reviews:</div>
                    <div class="stat-value">${countReviewsByRating(reviews1, 5)}</div>
                    <div class="stat-value">${countReviewsByRating(reviews2, 5)}</div>
                </div>
                <div class="stat-row">
                    <div class="stat-label">4★ Reviews:</div>
                    <div class="stat-value">${countReviewsByRating(reviews1, 4)}</div>
                    <div class="stat-value">${countReviewsByRating(reviews2, 4)}</div>
                </div>
                <div class="stat-row">
                    <div class="stat-label">3★ Reviews:</div>
                    <div class="stat-value">${countReviewsByRating(reviews1, 3)}</div>
                    <div class="stat-value">${countReviewsByRating(reviews2, 3)}</div>
                </div>
                <div class="stat-row">
                    <div class="stat-label">2★ Reviews:</div>
                    <div class="stat-value">${countReviewsByRating(reviews1, 2)}</div>
                    <div class="stat-value">${countReviewsByRating(reviews2, 2)}</div>
                </div>
                <div class="stat-row">
                    <div class="stat-label">1★ Reviews:</div>
                    <div class="stat-value">${countReviewsByRating(reviews1, 1)}</div>
                    <div class="stat-value">${countReviewsByRating(reviews2, 1)}</div>
                </div>
            </div>
        `;

        if (winner) {
            resultsHTML += `
                <div class="comparison-winner">
                    <h4>🏆 ${winnerName} wins with a higher rating!</h4>
                    <p>${winnerName} has a ${Math.abs(avgRating1 - avgRating2).toFixed(1)} higher star rating than ${loserName}.</p>
                </div>
            `;
        } else {
            resultsHTML += `
                <div class="comparison-winner">
                    <h4>🏆 It's a tie!</h4>
                    <p>Both restaurants have the same average rating of ${avgRating1} stars.</p>
                </div>
            `;
        }

        comparisonResults.innerHTML = resultsHTML;
        comparisonResults.classList.remove("hidden");
        comparisonResults.scrollIntoView({ behavior: "smooth" });
    }

    function generateStarsHTML(rating) {
        let html = '';
        for (let i = 1; i <= 5; i++) {
            html += `<span class="star${i <= rating ? " active" : ""}">★</span>`;
        }
        return html;
    }

    function countReviewsByRating(reviews, rating) {
        return reviews.filter(review => review.rating === rating).length;
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