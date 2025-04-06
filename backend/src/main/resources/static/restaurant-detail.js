document.addEventListener("DOMContentLoaded", () => {
    const restaurantDetailContainer = document.getElementById("restaurant-detail-container");
    const reviewsContainer = document.getElementById("reviews-container");
    const restaurantDetailTab = document.getElementById("restaurant-detail-tab");
    const reviewForm = document.getElementById("review-form");

    let existingUserReview = null;

    setupDarkMode();

    function setupDarkMode() {
        const darkModeToggle = document.getElementById("dark-mode-toggle");
        const darkModeEnabled = localStorage.getItem("darkModeEnabled") === "true";

        if (darkModeEnabled) {
            document.body.classList.add("dark-mode");
            darkModeToggle.checked = true;
        }

        darkModeToggle.addEventListener("change", () => {
            if (darkModeToggle.checked) {
                document.body.classList.add("dark-mode");
                localStorage.setItem("darkModeEnabled", "true");
            } else {
                document.body.classList.remove("dark-mode");
                localStorage.setItem("darkModeEnabled", "false");
            }
        });
    }

    const urlParams = new URLSearchParams(window.location.search);
    const restaurantId = urlParams.get("id");

    if (!restaurantId) {
        restaurantDetailContainer.innerHTML = '<div class="error">Restaurant ID not found.</div>';
        restaurantDetailTab.textContent = "ID Error";
        return;
    }

    fetch(`http://localhost:8080/api/restaurants/${restaurantId}`)
        .then(response => {
            if (!response.ok) throw new Error(`Status: ${response.status}`);
            return response.json();
        })
        .then(restaurant => {
            if (!restaurant) {
                restaurantDetailContainer.innerHTML = '<div class="error">Restaurant not found.</div>';
                restaurantDetailTab.textContent = "Not Found";
                return;
            }

            const name = restaurant.Name || restaurant.name || "Unnamed Restaurant";
            const type = restaurant.Category || restaurant.type || "Restaurant";
            const borough = restaurant.Borough || restaurant.borough || "";
            const imageUrl = restaurant.storePhoto ||
                "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg";

            document.title = `GoodEats - ${name}`;

            restaurantDetailContainer.innerHTML = `
                <h2>${name}</h2>
                <p>Type: ${type}</p>
                <p>Borough: ${borough}</p>
            `;
            restaurantDetailTab.innerHTML = `<a href="#">${name}</a>`;

            fetchReviews(restaurantId);
        })
        .catch(error => {
            console.error("Error fetching restaurant details:", error);
            restaurantDetailContainer.innerHTML = `<div class="error">Error loading restaurant details.</div>`;
            restaurantDetailTab.textContent = "Fetch Error";
        });

    setupReviewRatings();

    reviewForm.addEventListener("submit", async function (e) {
        e.preventDefault();
        const reviewText = document.getElementById("review-text").value;
        const rating = parseInt(document.getElementById("review-stars").dataset.rating);
        const userId = localStorage.getItem("userId");

        if (!userId) {
            alert("Please log in to submit a review.");
            return;
        }

        const payload = {
            userID: parseInt(userId),
            restaurantID: parseInt(restaurantId),
            review: reviewText,
            rating: rating
        };

        if (existingUserReview) {
            console.log("existingUserReview:", existingUserReview); // Log the whole object
            console.log("existingUserReview.id:", existingUserReview.id); // Log the id specifically
        } else {
            console.log("existingUserReview is null or undefined");
        }

        const method = existingUserReview ? "PUT" : "POST";
        const endpoint = existingUserReview
            ? `/api/reviews/${existingUserReview.id}`
            : `/api/reviews`;

        try {
            const response = await fetch(endpoint, {
                method: method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const data = await response.json();
            alert(existingUserReview ? "Review updated!" : "Review submitted!");
            await fetchReviews(restaurantId);
            existingUserReview = data;
        } catch (error) {
            console.error("Error submitting/updating review:", error);
            alert("There was an error. Please try again.");
        }
    });

    function fetchReviews(restaurantId) {
        const userId = localStorage.getItem("userId");

        fetch(`http://localhost:8080/api/restaurants/${restaurantId}/reviews`)
            .then(response => {
                if (!response.ok) throw new Error(`Status: ${response.status}`);
                return response.json();
            })
            .then(async reviews => {
                reviewsContainer.innerHTML = '<h3>Reviews:</h3>';

                if (!reviews || reviews.length === 0) {
                    reviewsContainer.innerHTML += "<p>No reviews yet.</p>";
                    return;
                }

                // Separate user’s review from the rest
                const otherReviews = [];
                let userReview = null;

                for (const review of reviews) {
                    if (userId && parseInt(userId) === review.userID) {
                        userReview = review;
                        existingUserReview = review;

                        // Pre-fill form
                        document.getElementById("review-text").value = review.review;
                        document.getElementById("rating").value = review.rating;
                        document.querySelector("#review-form button").textContent = "Edit Review";
                    } else {
                        otherReviews.push(review);
                    }
                }

                // Render user's review first
                if (userReview) {
                    const user = await fetchUser(userReview.userID);
                    reviewsContainer.appendChild(createReviewCard(user, userReview));
                }

                // Then render others
                for (const review of otherReviews) {
                    const user = await fetchUser(review.userID);
                    reviewsContainer.appendChild(createReviewCard(user, review));
                }
            })
            .catch(error => {
                console.error("Error fetching reviews:", error);
                reviewsContainer.innerHTML = '<h3>Reviews:</h3><p>Error loading reviews.</p>';
            });
    }

    function fetchUser(userId) {
        return fetch(`http://localhost:8080/api/users/${userId}`)
            .then(res => res.ok ? res.json() : null)
            .catch(err => {
                console.error("User fetch error:", err);
                return null;
            });
    }

    function createReviewCard(user, review) {
        const username = (user && user.username) ? user.username : `User ${review.userID}`;
        const stars = generateStars(review.rating);

        const reviewCard = document.createElement("div");
        reviewCard.classList.add("review-card");

        reviewCard.innerHTML = `
            <p><strong>${username}</strong></p>
            <div class="restaurant-rating">${stars}</div>
            <p><strong>Review:</strong> ${review.review}</p>
            <p><small>Date: ${new Date(review.date).toLocaleDateString()}</small></p>
        `;
        return reviewCard;
    }

    function generateStars(rating) {
        const starsContainer = document.createElement("div");
        starsContainer.classList.add("stars", "static-stars");

        for (let i = 1; i <= 5; i++) {
            const star = document.createElement("span");
            star.classList.add("star");
            if (i <= rating) star.classList.add("active");
            star.dataset.value = i;
            star.textContent = "★";
            starsContainer.appendChild(star);
        }
        return starsContainer.outerHTML;
    }

    function setupReviewRatings() {
        const starsContainer = document.getElementById("review-stars");

        starsContainer.querySelectorAll(".star").forEach((star) => {
            star.addEventListener("click", (e) => {
                e.stopPropagation();
                const value = parseInt(star.dataset.value);
                rateReview(value, starsContainer);
            });
        });
    }

    function rateReview(rating, starsContainer) {
        starsContainer.dataset.rating = rating;
        updateStars(starsContainer, rating);
    }

    function updateStars(starsContainer, rating) {
        const stars = starsContainer.querySelectorAll(".star");
        stars.forEach((star) => {
            const value = parseInt(star.dataset.value);
            if (value <= rating) {
                star.classList.add("active");
            } else {
                star.classList.remove("active");
            }
        });
    }
});