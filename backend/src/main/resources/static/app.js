document.addEventListener("DOMContentLoaded", () => {
    // Restaurant data logic
    const container = document.getElementById("restaurants-container");
    const boroughFilter = document.getElementById("borough-filter");
    const categoryFilter = document.getElementById("category-filter");
    const clearFiltersButton = document.getElementById("clear-filters");
    const searchInput = document.getElementById("search-input");
    const searchButton = document.getElementById("search-button");
    const darkModeToggle = document.getElementById("dark-mode-toggle");
    const recentlyViewedSection = document.getElementById("recently-viewed");

    // Store all restaurants for filtering
    let allRestaurants = []

    // Fetch restaurants from API
    fetch("http://localhost:5000/api/restaurants")
        .then((response) => {
            console.log("Response status:", response.status)

            if (!response.ok) {
                throw new Error(`Network response was not ok. Status: ${response.status}`)
            }
            return response.json()
        })
        .then((restaurants) => {
            console.log("Restaurants data:", restaurants)

            if (!Array.isArray(restaurants)) {
                console.error("Expected an array of restaurants but got:", typeof restaurants)
                container.innerHTML = '<div class="error">Invalid data format received from server.</div>'
                return
            }

            // Store all restaurants for filtering
            allRestaurants = restaurants

            // Initialise all features
            populateFilters(restaurants)
            displayRestaurants(restaurants)
            setupDarkMode()
            setupSearch()
            setupRecentlyViewed()
        })
        .catch((error) => {
            console.error("Error fetching restaurants:", error)

            container.innerHTML = `
                <div class="error">
                    <p>Error loading restaurants. Please try again later.</p>
                    <p class="error-details">${error.message}</p>
                    <button onclick="location.reload()">Retry</button>
                </div>
            `
        })

    // DARK MODE FUNCTIONALITY
    function setupDarkMode() {
        // Check if previously enabled dark mode
        const darkModeEnabled = localStorage.getItem("darkModeEnabled") === "true"

        if (darkModeEnabled) {
            document.body.classList.add("dark-mode")
            darkModeToggle.checked = true
        }

        darkModeToggle.addEventListener("change", () => {
            if (darkModeToggle.checked) {
                document.body.classList.add("dark-mode")
                localStorage.setItem("darkModeEnabled", "true")
            } else {
                document.body.classList.remove("dark-mode")
                localStorage.setItem("darkModeEnabled", "false")
            }
        })
    }

    // FILTER FUNCTIONALITY
    function populateFilters(restaurants) {
        const boroughs = new Set()
        const categories = new Set()

        restaurants.forEach((restaurant) => {
            // Handle field name inconsistencies
            const borough = restaurant.Borough || restaurant.borough || ""
            const category = restaurant.Category || restaurant.type || ""

            if (borough) boroughs.add(borough)
            if (category) categories.add(category)
        })

        // Sort values alphabetically
        const sortedBoroughs = Array.from(boroughs).sort()
        const sortedCategories = Array.from(categories).sort()

        sortedBoroughs.forEach((borough) => {
            const option = document.createElement("option")
            option.value = borough
            option.textContent = borough
            boroughFilter.appendChild(option)
        })

        sortedCategories.forEach((category) => {
            const option = document.createElement("option")
            option.value = category
            option.textContent = category
            categoryFilter.appendChild(option)
        })

        boroughFilter.addEventListener("change", applyFilters)
        categoryFilter.addEventListener("change", applyFilters)

        clearFiltersButton.addEventListener("click", () => {
            boroughFilter.value = ""
            categoryFilter.value = ""
            searchInput.value = ""
            applyFilters()
        })
    }

    // SEARCH FUNCTIONALITY
    function setupSearch() {
        // Search when button is clicked
        searchButton.addEventListener("click", () => {
            applyFilters()
        })

        // Search when Enter key is pressed
        searchInput.addEventListener("keypress", (e) => {
            if (e.key === "Enter") {
                applyFilters()
            }
        })
    }

    function applyFilters() {
        const selectedBorough = boroughFilter.value
        const selectedCategory = categoryFilter.value
        const searchQuery = searchInput.value.toLowerCase().trim()

        const filteredRestaurants = allRestaurants.filter((restaurant) => {
            // Handle field name inconsistencies
            const borough = restaurant.Borough || restaurant.borough || ""
            const category = restaurant.Category || restaurant.type || ""
            const name = (restaurant.Name || restaurant.name || "").toLowerCase()

            const boroughMatch = !selectedBorough || borough === selectedBorough

            const categoryMatch = !selectedCategory || category === selectedCategory

            const searchMatch =
                !searchQuery ||
                name.includes(searchQuery) ||
                category.toLowerCase().includes(searchQuery) ||
                borough.toLowerCase().includes(searchQuery)

            return boroughMatch && categoryMatch && searchMatch
        })

        // Display filtered restaurants
        displayRestaurants(filteredRestaurants)
    }

    // DISPLAY RESTAURANTS
    async function displayRestaurants(restaurants) {
        const userId = localStorage.getItem("userId");
        const favouriteIds = {};

        container.innerHTML = ""

        if (!restaurants || restaurants.length === 0) {
            container.innerHTML = '<div class="no-results">No restaurants found matching your filters.</div>'
            return
        }

        for (const restaurant of restaurants) {
            const name = restaurant.Name || restaurant.name || "Unnamed Restaurant";
            const type = restaurant.Category || restaurant.type || "Restaurant";
            const borough = restaurant.Borough || restaurant.borough || "";
            let imageUrl = restaurant.storePhoto || "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg";
            const restaurantId = restaurant.id || restaurant._id;

            if (restaurantId == 2) {
                imageUrl = "/GScanteen.webp";
            } else if (restaurantId == 1) {
                imageUrl = "/wasabi.jpg";
            } else if (restaurantId == 3) {
                imageUrl = "/CiaoBella.jpg";
            } else if (restaurantId == 4) {
                imageUrl = "/braza.jpg";
            } else if (restaurantId == 5) {
                imageUrl = "/Frankie.avif";
            }

            let isFavourite = false;
            if (userId) {
                const reviewRes = await fetch(`http://localhost:5000/api/restaurants/${restaurantId}/reviews`);
                const reviews = await reviewRes.json();
                const userReview = reviews.find(r => r.userID === parseInt(userId));
                isFavourite = userReview?.favourite === true;
            }

            const card = document.createElement("div");
            card.className = "restaurant-card";

            card.innerHTML = `
                <img src="${imageUrl}" alt="${name}" class="restaurant-image">
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
                    <button class="favourite-button ${isFavourite ? "active" : ""}" data-id="${restaurantId}">★</button>
                </div>
            `;

            card.addEventListener("click", (e) => {
                console.log("Card clicked:", name);

                if (e.target.closest(".favourite-button") || e.target.closest(".star")) {
                    return;
                }

                addToRecentlyViewed(restaurant);

                const restId = restaurant.id || restaurant._id;
                if (!restId) {
                    console.error("Missing restaurant ID for redirect.");
                    return;
                }

                window.location.href = `http://localhost:5000/restaurant-detail.html?id=${restId}`;
            });

            setupRatingCard(card, restaurantId);

            container.appendChild(card);

            const favouriteButton = card.querySelector(".favourite-button");
            favouriteButton.addEventListener("click", (e) => {
                e.stopPropagation(); // Prevent triggering the card redirect
                toggleFavourite(restaurant, favouriteButton); // This should still be defined
            });
        }
    }

    // FAVOURITE FUNCTIONALITY
    function toggleFavourite(restaurant, button) {
        const userId = localStorage.getItem("userId");
        if (!userId) return alert("Login to manage favourites");

        const restaurantId = restaurant.id || restaurant._id;

        fetch(`http://localhost:5000/api/restaurants/${restaurantId}/reviews`)
            .then(res => res.json())
            .then(reviews => {
                const userReview = reviews.find(r => r.userID === parseInt(userId));

                if (userReview) {
                    // Toggle favourite on existing review
                    const newFavourite = !userReview.favourite;

                    return fetch(`http://localhost:5000/api/reviews/${userReview.id}/favourite`, {
                        method: "PUT",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ favourite: newFavourite })
                    }).then(res => {
                        if (res.ok) {
                            button.classList.toggle("active", newFavourite);
                        }
                    });
                } else {
                    // Create a new blank review with favourite = true
                    const payload = {
                        userID: parseInt(userId),
                        restaurantID: parseInt(restaurantId),
                        review: "",
                        rating: 0,
                        favourite: true
                    };

                    return fetch("http://localhost:5000/api/reviews", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(payload)
                    }).then(res => {
                        if (res.ok) {
                            button.classList.add("active");
                        }
                    });
                }
            })
            .catch(err => {
                console.error("Failed to toggle favourite:", err);
                alert("Something went wrong trying to favourite this restaurant.");
            });
    }

    function updateStars(container, rating) {
        const rounded = Math.round(rating);
        container.dataset.rating = rounded;
        container.querySelectorAll(".star").forEach(star => {
            star.classList.toggle("active", parseInt(star.dataset.value) <= rounded);
        });
    }

    function setupRatingCard(card, restaurantId) {
        const userId = localStorage.getItem("userId");
        const starsContainer = card.querySelector(".stars");
        const ratingText = card.querySelector(".rating-count");

        fetch(`http://localhost:5000/api/restaurants/${restaurantId}/reviews`)
            .then(res => res.json())
            .then(reviews => {
                const total = reviews.reduce((acc, r) => acc + (r.rating || 0), 0);
                const avg = reviews.length > 0 ? (total / reviews.length).toFixed(1) : 0;

                const existingAvg = card.querySelector(".avg-rating");
                if (existingAvg) existingAvg.remove();

                const nameEl = card.querySelector(".restaurant-name");
                const avgSpan = document.createElement("span");
                avgSpan.className = "avg-rating";
                avgSpan.style.fontSize = "14px";
                avgSpan.style.fontWeight = "normal";
                avgSpan.style.marginLeft = "10px";
                avgSpan.textContent = `(Average ${avg} Stars)`;
                nameEl.appendChild(avgSpan);

                updateStars(starsContainer, 0);
                ratingText.textContent = `(${reviews.length} rating${reviews.length !== 1 ? "s" : ""})`;

                if (userId) {
                    const existing = reviews.find(r => r.userID === parseInt(userId));
                    if (existing) {
                        starsContainer.dataset.userReviewId = existing.id;
                        updateStars(starsContainer, existing.rating);
                    }

                    // Allow the logged-in user to rate
                    starsContainer.querySelectorAll(".star").forEach(star => {
                        star.addEventListener("click", e => {
                            e.stopPropagation();
                            const value = parseInt(star.dataset.value);
                            const reviewId = starsContainer.dataset.userReviewId;

                            const payload = {
                                userID: parseInt(userId),
                                restaurantID: parseInt(restaurantId),
                                review: "",
                                rating: value
                            };

                            const url = reviewId ? `/api/reviews/${reviewId}` : `/api/reviews`;
                            const method = reviewId ? "PUT" : "POST";

                            fetch(url, {
                                method,
                                headers: { "Content-Type": "application/json" },
                                body: JSON.stringify(payload)
                            })
                                .then(res => res.json())
                                .then(() => {
                                    showRatingConfirmation(value);
                                    setupRatingCard(card, restaurantId); // refresh
                                })
                                .catch(err => {
                                    console.error("Rating error:", err);
                                    alert("Failed to submit rating.");
                                });
                        });
                    });
                } else {
                    // Prevent interaction for non-logged-in users
                    starsContainer.style.pointerEvents = "none";
                }
            });
    }

    function showRatingConfirmation(rating) {
        const msg = document.createElement("div");
        msg.textContent = `Thanks for rating ${rating} stars!`;
        msg.className = "rating-confirmation";
        document.body.appendChild(msg);
        setTimeout(() => msg.remove(), 2500);
    }

    // RECENTLY VIEWED FUNCTIONALITY
    function setupRecentlyViewed() {
        const recentlyViewed = JSON.parse(localStorage.getItem("goodEatsRecentlyViewed")) || []

        if (recentlyViewed.length === 0) {
            recentlyViewedSection.classList.add("hidden")
            return
        }

        recentlyViewedSection.classList.remove("hidden")

        const recentContainer = document.querySelector(".recent-restaurants-container")
        recentContainer.innerHTML = ""

        // Display up to 5 most recent restaurants
        recentlyViewed.slice(0, 5).forEach((restaurant) => {
            const name = restaurant.Name || restaurant.name || "Unnamed Restaurant"
            const type = restaurant.Category || restaurant.type || "Restaurant"
            let imageUrl =
                restaurant.storePhoto ||
                "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg"
            const restaurantId = restaurant.id || restaurant._id;

            if (restaurantId == 2) {
                imageUrl = "/GScanteen.webp";
            } else if (restaurantId == 1) {
                imageUrl = "/wasabi.jpg";
            } else if (restaurantId == 3) {
                imageUrl = "/CiaoBella.jpg";
            } else if (restaurantId == 4) {
                imageUrl = "/braza.jpg";
            } else if (restaurantId == 5) {
                imageUrl = "/Frankie.avif";
            }


            const card = document.createElement("div")
            card.className = "recent-restaurant-card"
            card.innerHTML = `
                <img src="${imageUrl}" alt="${name}" class="recent-restaurant-image" 
                    onerror="this.src='https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg'">
                <div class="recent-restaurant-info">
                    <div class="recent-restaurant-name">${name}</div>
                    <div class="recent-restaurant-type">${type}</div>
                </div>
            `

            card.addEventListener("click", () => {
                const restaurantId = restaurant.id || restaurant._id
                const mainCard = document
                    .querySelector(`.restaurant-card .favourite-button[data-id="${restaurantId}"]`)
                    ?.closest(".restaurant-card")

                if (mainCard) {
                    mainCard.scrollIntoView({behavior: "smooth"})
                    mainCard.classList.add("highlight")
                    setTimeout(() => {
                        mainCard.classList.remove("highlight")
                    }, 2000)
                }
            })

            recentContainer.appendChild(card)
        })
    }

    function addToRecentlyViewed(restaurant) {
        // Get existing recently viewed
        const recentlyViewed = JSON.parse(localStorage.getItem("goodEatsRecentlyViewed")) || []

        // Remove if already in list
        const restaurantId = restaurant.id || restaurant._id
        const existingIndex = recentlyViewed.findIndex((r) => {
            const id = r.id || r._id
            return id === restaurantId
        })

        if (existingIndex >= 0) {
            recentlyViewed.splice(existingIndex, 1)
        }

        recentlyViewed.unshift(restaurant)

        const trimmedList = recentlyViewed.slice(0, 10)

        localStorage.setItem("goodEatsRecentlyViewed", JSON.stringify(trimmedList))

        setupRecentlyViewed()
    }
})