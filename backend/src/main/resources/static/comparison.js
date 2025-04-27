document.addEventListener("DOMContentLoaded", () => {
    const darkModeToggle = document.getElementById("dark-mode-toggle");
    const restaurantsContainer = document.getElementById("restaurants-container");
    const compareButton = document.getElementById("compare-button");
    const clearButton = document.getElementById("clear-comparison");
    const comparisonResults = document.getElementById("comparison-results");

    // Travel time elements
    const postcodeInput = document.getElementById("user-postcode");
    const travelModeSelect = document.getElementById("travel-mode");
    const calculateDistanceBtn = document.getElementById("calculate-distance");

    // OpenRouteService API key - you'll need to replace this with your actual API key
    const ORS_API_KEY = "5b3ce3597851110001cf62480278cea0a7e148b2bb33628a85ea9421";

    // Store all restaurants in memory
    let allRestaurants = [];

    // Travel data storage
    let travelData = {
        userPostcode: null,
        travelMode: null,
        restaurant1: { time: null, distance: null },
        restaurant2: { time: null, distance: null }
    };

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
        calculateDistanceBtn.addEventListener("click", calculateTravelTimes);
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
                // Store restaurants in memory for later use
                allRestaurants = restaurants;
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

        // Reset travel data for this restaurant
        travelData[`restaurant${slot}`] = { time: null, distance: null };
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

        // Reset travel data for this restaurant
        travelData[`restaurant${slot}`] = { time: null, distance: null };
    }

    function clearComparison() {
        removeRestaurant(1);
        removeRestaurant(2);
        comparisonResults.classList.add("hidden");

        // Reset all travel data
        travelData = {
            userPostcode: null,
            travelMode: null,
            restaurant1: { time: null, distance: null },
            restaurant2: { time: null, distance: null }
        };
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

    async function calculateTravelTimes() {
        const postcode = postcodeInput.value.trim();
        const travelMode = travelModeSelect.value;

        if (!postcode) {
            alert("Please enter your postcode");
            postcodeInput.focus();
            return;
        }

        const restaurant1El = document.querySelector("#restaurant1-selection .selected-restaurant-card");
        const restaurant2El = document.querySelector("#restaurant2-selection .selected-restaurant-card");

        if (!restaurant1El || !restaurant2El) {
            alert("Please select two restaurants to compare first");
            return;
        }

        // Store user input
        travelData.userPostcode = postcode;
        travelData.travelMode = travelMode;

        // Show loading state
        calculateDistanceBtn.textContent = "Calculating...";
        calculateDistanceBtn.disabled = true;

        try {
            // Get restaurants from the stored array using their IDs
            const id1 = restaurant1El.dataset.id;
            const id2 = restaurant2El.dataset.id;

            // Find restaurants in our stored array (instead of making API calls)
            const restaurant1 = findRestaurantById(id1);
            const restaurant2 = findRestaurantById(id2);

            if (!restaurant1 || !restaurant2) {
                throw new Error("Could not find restaurant details");
            }

            // Convert postcodes to coordinates using an external API
            const [userCoords, restaurant1Coords, restaurant2Coords] = await Promise.all([
                getCoordinatesFromPostcode(postcode),
                getCoordinatesFromPostcode(restaurant1.postcode),
                getCoordinatesFromPostcode(restaurant2.postcode)
            ]);

            // Get travel data from OpenRouteService API
            const [route1Data, route2Data] = await Promise.all([
                getRouteData(userCoords, restaurant1Coords, travelMode),
                getRouteData(userCoords, restaurant2Coords, travelMode)
            ]);

            // Store the travel data
            travelData.restaurant1 = {
                time: route1Data.time,
                distance: route1Data.distance
            };

            travelData.restaurant2 = {
                time: route2Data.time,
                distance: route2Data.distance
            };

            // Update the comparison results with travel data
            updateComparisonWithTravelData();

        } catch (error) {
            console.error("Error calculating travel times:", error);
            alert("An error occurred while calculating travel times. Please check your postcode and try again.");
        } finally {
            // Reset button state
            calculateDistanceBtn.textContent = "Calculate Travel Times";
            calculateDistanceBtn.disabled = false;
        }
    }

    // Helper function to find a restaurant by ID in our stored array
    function findRestaurantById(id) {
        return allRestaurants.find(restaurant => {
            const restaurantId = restaurant.id || restaurant._id;
            return restaurantId == id; // Using == instead of === for string/number comparison
        });
    }

    async function getCoordinatesFromPostcode(postcode) {
        try {
            // Use postcodes.io API to convert UK postcodes to coordinates
            const response = await fetch(`https://api.postcodes.io/postcodes/${encodeURIComponent(postcode)}`);
            const data = await response.json();

            if (data.status === 200 && data.result) {
                return {
                    longitude: data.result.longitude,
                    latitude: data.result.latitude
                };
            } else {
                throw new Error(`Could not find coordinates for postcode: ${postcode}`);
            }
        } catch (error) {
            console.error("Error getting coordinates:", error);
            // Fall back to dummy coordinates for demonstration purposes
            return simulateCoordinatesFromPostcode(postcode);
        }
    }

    function simulateCoordinatesFromPostcode(postcode) {
        // This is a fallback when the postcode API is unavailable
        // In a real app, you should handle this more gracefully
        console.warn("Using simulated coordinates for postcode:", postcode);

        // Central London coordinates as base
        const baseLat = 51.5074;
        const baseLng = -0.1278;

        // Generate a small random offset based on postcode to simulate different locations
        const postcodeSeed = postcode.replace(/\s+/g, '').split('').reduce((acc, char) => {
            return acc + char.charCodeAt(0);
        }, 0);

        const latOffset = (postcodeSeed % 100) / 500; // Small variation
        const lngOffset = (postcodeSeed % 100) / 700; // Small variation

        return {
            latitude: baseLat + latOffset,
            longitude: baseLng + lngOffset
        };
    }

    async function getRouteData(start, end, travelMode) {
        try {
            // Call OpenRouteService API to get route information
            const body = {
                coordinates: [
                    [start.longitude, start.latitude],
                    [end.longitude, end.latitude]
                ],
                radiuses: [-1, -1]
            };

            const response = await fetch(`https://api.openrouteservice.org/v2/directions/${travelMode}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorisation': ORS_API_KEY
                },
                body: JSON.stringify(body)
            });

            const data = await response.json();

            if (data.features && data.features.length > 0) {
                const route = data.features[0].properties.segments[0];

                // Return time in minutes and distance in kilometers
                return {
                    time: Math.round(route.duration / 60),  // Convert seconds to minutes
                    distance: (route.distance / 1000).toFixed(1)  // Convert meters to kilometers
                };
            } else {
                throw new Error("No route found");
            }
        } catch (error) {
            console.error("Error getting route data:", error);
            // Fall back to simulated route data
            return simulateRouteData(start, end, travelMode);
        }
    }

    function simulateRouteData(start, end, travelMode) {
        // Calculate Haversine distance between two points
        const R = 6371; // Earth's radius in km
        const dLat = (end.latitude - start.latitude) * Math.PI / 180;
        const dLon = (end.longitude - start.longitude) * Math.PI / 180;
        const a =
            Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(start.latitude * Math.PI / 180) * Math.cos(end.latitude * Math.PI / 180) *
            Math.sin(dLon/2) * Math.sin(dLon/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        const distance = R * c;

        // Simulate travel time based on distance and mode
        let speed; // km per hour
        switch(travelMode) {
            case 'foot-walking':
                speed = 5;
                break;
            case 'cycling-regular':
                speed = 15;
                break;
            case 'driving-car':
            default:
                speed = 30; // City driving with traffic
                break;
        }

        // Calculate time in minutes
        const time = Math.round((distance / speed) * 60);

        return {
            distance: distance.toFixed(1),
            time: time
        };
    }

    function updateComparisonWithTravelData() {
        // Only proceed if we already have comparison results displayed
        if (comparisonResults.classList.contains("hidden")) {
            compareRestaurants();
            return;
        }

        // Find the travel time section or create it if it doesn't exist
        let travelSection = comparisonResults.querySelector(".travel-comparison");
        if (!travelSection) {
            // Create the travel section
            travelSection = document.createElement("div");
            travelSection.className = "travel-comparison";

            // Find where to insert it (after the comparison stats)
            const comparisonStats = comparisonResults.querySelector(".comparison-stats");
            if (comparisonStats) {
                comparisonStats.insertAdjacentElement('afterend', travelSection);
            } else {
                comparisonResults.appendChild(travelSection);
            }
        }

        // Get restaurant names
        const restaurant1Name = document.querySelector("#restaurant1-selection .selected-restaurant-card h4").textContent;
        const restaurant2Name = document.querySelector("#restaurant2-selection .selected-restaurant-card h4").textContent;

        // Display travel mode in a readable format
        let travelModeText;
        switch(travelData.travelMode) {
            case 'foot-walking':
                travelModeText = 'Walking';
                break;
            case 'cycling-regular':
                travelModeText = 'Cycling';
                break;
            case 'driving-car':
                travelModeText = 'Driving';
                break;
            default:
                travelModeText = travelData.travelMode;
        }

        // Determine which restaurant is closer in terms of time
        let closerRestaurant = null;
        let timeDifference = 0;

        if (travelData.restaurant1.time < travelData.restaurant2.time) {
            closerRestaurant = restaurant1Name;
            timeDifference = travelData.restaurant2.time - travelData.restaurant1.time;
        } else if (travelData.restaurant2.time < travelData.restaurant1.time) {
            closerRestaurant = restaurant2Name;
            timeDifference = travelData.restaurant1.time - travelData.restaurant2.time;
        }

        // Update the travel section content
        travelSection.innerHTML = `
            <h3>Travel Information</h3>
            <p class="travel-from">From postcode: <strong>${travelData.userPostcode}</strong> by <strong>${travelModeText}</strong></p>
            <div class="comparison-stats">
                <div class="stat-row">
                    <div class="stat-label">Travel Time:</div>
                    <div class="stat-value">
                        ${travelData.restaurant1.time} min
                    </div>
                    <div class="stat-value">
                        ${travelData.restaurant2.time} min
                    </div>
                </div>
                <div class="stat-row">
                    <div class="stat-label">Distance:</div>
                    <div class="stat-value">
                        ${travelData.restaurant1.distance} km
                    </div>
                    <div class="stat-value">
                        ${travelData.restaurant2.distance} km
                    </div>
                </div>
            </div>
            ${closerRestaurant ? `
                <div class="comparison-winner travel-winner">
                    <h4>🚀 ${closerRestaurant} is closer!</h4>
                    <p>It would save you ${timeDifference} minutes of travel time.</p>
                </div>
            ` : `
                <div class="comparison-winner travel-winner">
                    <h4>🚀 Same travel time!</h4>
                    <p>Both restaurants are equally accessible from your location.</p>
                </div>
            `}
        `;

        // Make sure the comparison results are visible
        comparisonResults.classList.remove("hidden");

        // Scroll to the travel section
        travelSection.scrollIntoView({ behavior: "smooth" });
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

        // If we have travel data, update the comparison with it
        if (travelData.userPostcode && travelData.restaurant1.time !== null && travelData.restaurant2.time !== null) {
            updateComparisonWithTravelData();
        }

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