document.addEventListener("DOMContentLoaded", () => {
    const favouritesContainer = document.getElementById("favourites-container");
    const darkModeToggle = document.getElementById("dark-mode-toggle");

    setupDarkMode(darkModeToggle);

    const userId = localStorage.getItem("userId");
    if (!userId) {
        favouritesContainer.innerHTML = `
            <div class="no-favourites">
                <h2>Your Favourites</h2>
                <p>Please log in to view your favourite restaurants.</p>
                <a href="index.html" class="button">Browse Restaurants</a>
            </div>
        `;
        return;
    }

    // Fetch favourites from backend
    fetch(`http://34.142.84.120:5000/api/reviews/favourites?userId=${userId}`)
        .then(res => {
            if (!res.ok) throw new Error("Failed to fetch favourites");
            return res.json();
        })
        .then(favourites => {
            if (!favourites || favourites.length === 0) {
                favouritesContainer.innerHTML = `
                    <div class="no-favourites">
                        <h2>Your Favourites</h2>
                        <p>You haven't added any favourite restaurants yet.</p>
                        <a href="index.html" class="button">Browse Restaurants</a>
                    </div>
                `;
                return;
            }

            favouritesContainer.innerHTML = "";
            favourites.forEach(restaurant => {
                const card = createRestaurantCard(restaurant);
                favouritesContainer.appendChild(card);
                setupRatingCard(card, restaurant.id || restaurant._id);
            });
        })
        .catch(err => {
            console.error("Error loading favourites:", err);
            favouritesContainer.innerHTML = `
                <div class="error">
                    <p>Failed to load your favourites. Please try again later.</p>
                </div>
            `;
        });
});

// DARK MODE FUNCTIONALITY
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

// DISPLAY RATING FROM BACKEND
function setupRatingCard(card, restaurantId) {
    const userId = localStorage.getItem("userId");
    const starsContainer = card.querySelector(".stars");
    const ratingText = card.querySelector(".rating-count");

    fetch(`http://34.142.84.120:5000/api/restaurants/${restaurantId}/reviews`)
        .then(res => res.json())
        .then(reviews => {
            const total = reviews.reduce((sum, r) => sum + (r.rating || 0), 0);
            const avg = reviews.length ? (total / reviews.length).toFixed(1) : "0.0";

            const nameEl = card.querySelector(".restaurant-name");
            const existingAvg = card.querySelector(".avg-rating");
            if (existingAvg) existingAvg.remove();

            const avgSpan = document.createElement("span");
            avgSpan.className = "avg-rating";
            avgSpan.style.fontSize = "14px";
            avgSpan.style.fontWeight = "normal";
            avgSpan.style.marginLeft = "10px";
            avgSpan.textContent = `(Average ${avg} Stars)`;
            nameEl.appendChild(avgSpan);

            updateStars(starsContainer, 0); // Start fresh
            ratingText.textContent = `(${reviews.length} rating${reviews.length !== 1 ? "s" : ""})`;

            if (userId) {
                const existing = reviews.find(r => r.userID === parseInt(userId));
                if (existing) {
                    starsContainer.dataset.userReviewId = existing.id;
                    updateStars(starsContainer, existing.rating);
                }

                starsContainer.querySelectorAll(".star").forEach(star => {
                    star.addEventListener("click", (e) => {
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
                                setupRatingCard(card, restaurantId); // Refresh
                            })
                            .catch(err => {
                                console.error("Rating error:", err);
                                alert("Failed to submit rating.");
                            });
                    });
                });
            } else {
                starsContainer.style.pointerEvents = "none";
            }
        });
}

function updateStars(container, rating) {
    const rounded = Math.round(rating);
    container.dataset.rating = rounded;
    container.querySelectorAll(".star").forEach(star => {
        star.classList.toggle("active", parseInt(star.dataset.value) <= rounded);
    });
}

function showRatingConfirmation(rating) {
    const message = document.createElement("div");
    message.className = "rating-confirmation";
    message.textContent = `Thanks for rating ${rating} stars!`;
    Object.assign(message.style, {
        position: "fixed",
        bottom: "20px",
        right: "20px",
        backgroundColor: "#ff6b6b",
        color: "white",
        padding: "10px 20px",
        borderRadius: "4px",
        boxShadow: "0 2px 10px rgba(0, 0, 0, 0.2)",
        zIndex: "1000"
    });
    document.body.appendChild(message);
    setTimeout(() => {
        message.style.opacity = "0";
        message.style.transition = "opacity 0.5s";
        setTimeout(() => document.body.removeChild(message), 500);
    }, 3000);
}

function createRestaurantCard(restaurant) {
    const card = document.createElement("div");
    card.className = "restaurant-card";

    const name = restaurant.name || restaurant.Name || "Unnamed Restaurant";
    const type = restaurant.category || restaurant.type || restaurant.Category || "Restaurant";
    const borough = restaurant.borough || restaurant.Borough || "";
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

    card.innerHTML = `
        <img src="${imageUrl}" alt="${name}" class="restaurant-image"
             onerror="this.src='https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg'">
        <div class="restaurant-info">
            <h2 class="restaurant-name">${name}</h2>
            <span class="restaurant-type">${type}</span>
            ${borough ? `<p class="restaurant-borough">${borough}</p>` : ""}
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
            <button class="favourite-button active" data-id="${restaurantId}">★</button>
        </div>
    `;

    card.addEventListener("click", (e) => {
        if (e.target.closest(".favourite-button") || e.target.closest(".star")) {
            return; // prevent conflict with rating/favourite click
        }
        window.location.href = `restaurant-detail.html?id=${restaurantId}`;
    });

    const favButton = card.querySelector(".favourite-button");
    favButton.addEventListener("click", (e) => {
        e.stopPropagation(); // prevent card click
        unfavouriteRestaurant(restaurant, card);
    });

    return card;
}

function unfavouriteRestaurant(restaurant, card) {
    const userId = localStorage.getItem("userId");
    const restaurantId = restaurant.id || restaurant._id;

    fetch(`http://34.142.84.120:5000/api/restaurants/${restaurantId}/reviews`)
        .then(res => res.json())
        .then(reviews => {
            const userReview = reviews.find(r => r.userID === parseInt(userId));
            if (!userReview) return;

            fetch(`http://34.142.84.120:5000/api/reviews/${userReview.id}/favourite`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ favourite: false })
            })
                .then(() => {
                    card.remove();
                    const remaining = document.querySelectorAll(".restaurant-card");
                    if (remaining.length === 0) {
                        document.getElementById("favourites-container").innerHTML = `
                            <div class="no-favourites">
                                <h2>Your Favourites</h2>
                                <p>You haven't added any favourite restaurants yet.</p>
                                <a href="index.html" class="button">Browse Restaurants</a>
                            </div>
                        `;
                    }
                });
        });
}