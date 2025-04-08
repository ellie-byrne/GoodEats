document.addEventListener("DOMContentLoaded", () => {
    const restaurantDetailContainer = document.getElementById("restaurant-detail-container");
    const userReviewContainer = document.getElementById("user-review-container");
    const reviewsGrid = document.querySelector(".review-grid");
    const restaurantDetailTab = document.getElementById("restaurant-detail-tab");

    setupDarkMode();

    const urlParams = new URLSearchParams(window.location.search);
    const restaurantId = urlParams.get("id");
    const userId = localStorage.getItem("userId");

    let userReview = null;

    if (!restaurantId) {
        restaurantDetailContainer.innerHTML = '<div class="error">Restaurant ID not found.</div>';
        return;
    }

    fetch(`http://localhost:8080/api/restaurants/${restaurantId}`)
        .then(res => res.json())
        .then(restaurant => {
            const name = restaurant.name || "Unnamed Restaurant";
            const borough = restaurant.borough || "";
            const type = restaurant.type || "Restaurant";
            const photo = restaurant.storePhoto || "https://via.placeholder.com/300x200?text=Restaurant+Photo";

            restaurantDetailContainer.innerHTML = `
                <img src="${photo}" alt="${name}" class="restaurant-image">
                <h2>${name}</h2>
                <p>Type: ${type}</p>
                <p>Borough: ${borough}</p>
            `;
            restaurantDetailTab.innerHTML = `<a href="#">${name}</a>`;
        });

    fetch(`http://localhost:8080/api/restaurants/${restaurantId}/reviews`)
        .then(res => res.json())
        .then(reviews => {
            reviewsGrid.innerHTML = "";
            const otherReviews = [];

            reviews.forEach(review => {
                if (parseInt(userId) === review.userID) {
                    userReview = review;
                } else {
                    otherReviews.push(review);
                }
            });

            renderUserReviewSection(userReview);
            renderReviews(otherReviews);
        });

    function renderUserReviewSection(review) {
        userReviewContainer.innerHTML = "";

        if (!userId) {
            renderReviewForm(); // Not logged in
        } else if (review) {
            const stars = generateStars(review.rating);
            userReviewContainer.innerHTML = `
                <div class="user-review-box">
                    <h3>Your Review</h3>
                    <p>${review.review}</p>
                    <div class="restaurant-rating">${stars}</div>
                    <div class="review-photo-placeholder">[ Photo Placeholder ]</div>
                    <button id="edit-review-btn">Edit Review</button>
                    <button id="delete-review-btn" class="danger">Delete Review</button>
                </div>
            `;

            document.getElementById("edit-review-btn").addEventListener("click", () => {
                renderReviewForm(true);
            });

            document.getElementById("delete-review-btn").addEventListener("click", () => {
                if (confirm("Are you sure you want to delete your review?")) {
                    fetch(`/api/reviews/${review.id}`, {
                        method: "DELETE"
                    })
                        .then(res => {
                            if (res.ok) {
                                location.reload();
                            } else {
                                alert("Failed to delete review.");
                            }
                        })
                        .catch(err => {
                            console.error("Delete error:", err);
                            alert("Error deleting review.");
                        });
                }
            });
        } else {
            renderReviewForm(); // Logged in, no review
        }
    }

    function renderReviewForm(isEditing = false) {
        userReviewContainer.innerHTML = `
            <h3>${isEditing ? "Edit Review" : "Submit a Review"}</h3>
            <form id="review-form">
                <textarea id="review-text" placeholder="Write your review here..." required>${isEditing ? userReview.review : ""}</textarea>
                <label>Rating:</label>
                <div class="restaurant-rating">
                    <div class="stars" id="review-stars" data-rating="${isEditing ? userReview.rating : 0}">
                        ${[1,2,3,4,5].map(i => `<span class="star ${i <= (isEditing ? userReview.rating : 0) ? 'active' : ''}" data-value="${i}">★</span>`).join('')}
                    </div>
                </div>
                <button type="submit">${isEditing ? "Update" : "Submit"}</button>
            </form>
        `;

        setupStarRating();

        document.getElementById("review-form").addEventListener("submit", (e) => {
            e.preventDefault();
            const review = document.getElementById("review-text").value;
            const rating = parseInt(document.getElementById("review-stars").dataset.rating);

            const payload = {
                userID: parseInt(userId),
                restaurantID: parseInt(restaurantId),
                review,
                rating
            };

            const method = isEditing ? "PUT" : "POST";
            const endpoint = isEditing ? `/api/reviews/${userReview.id}` : "/api/reviews";

            fetch(endpoint, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            })
                .then(res => res.json())
                .then(() => location.reload())
                .catch(err => alert("Something went wrong."));
        });
    }

    function renderReviews(reviews) {
        const filteredReviews = reviews.filter(r => r.review && r.review.trim() !== "");

        if (!filteredReviews || filteredReviews.length === 0) {
            reviewsGrid.innerHTML = "<p>No reviews yet.</p>";
            return;
        }

        filteredReviews.forEach(review => {
            fetch(`http://localhost:8080/api/users/${review.userID}`)
                .then(res => res.json())
                .then(user => {
                    const card = document.createElement("div");
                    card.classList.add("review-card");
                    card.innerHTML = `
                        <p><strong>${user?.username || "User " + review.userID}</strong></p>
                        <div class="restaurant-rating">${generateStars(review.rating)}</div>
                        <p><strong>Review:</strong> ${review.review}</p>
                        <p><small>Date: ${new Date(review.date).toLocaleDateString()}</small></p>
                        <div class="review-photo-placeholder">[ Photo Placeholder ]</div>
                    `;
                    reviewsGrid.appendChild(card);
                });
        });
    }

    function generateStars(rating) {
        return `<div class="stars static-stars">${[1, 2, 3, 4, 5].map(i =>
            `<span class="star${i <= rating ? " active" : ""}">★</span>`).join("")}</div>`;
    }

    function setupStarRating() {
        const starsContainer = document.getElementById("review-stars");
        const stars = starsContainer.querySelectorAll(".star");

        stars.forEach(star => {
            star.addEventListener("click", () => {
                const value = parseInt(star.dataset.value);
                starsContainer.dataset.rating = value;
                stars.forEach(s => s.classList.toggle("active", parseInt(s.dataset.value) <= value));
            });
        });
    }

    function setupDarkMode() {
        const toggle = document.getElementById("dark-mode-toggle");
        const enabled = localStorage.getItem("darkModeEnabled") === "true";
        if (enabled) {
            document.body.classList.add("dark-mode");
            toggle.checked = true;
        }

        toggle.addEventListener("change", () => {
            document.body.classList.toggle("dark-mode");
            localStorage.setItem("darkModeEnabled", toggle.checked);
        });
    }
});