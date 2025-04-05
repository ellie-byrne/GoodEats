document.addEventListener("DOMContentLoaded", () => {
    const restaurantDetailContainer = document.getElementById("restaurant-detail-container");
    const reviewsContainer = document.getElementById("reviews-container");
    const restaurantDetailTab = document.getElementById("restaurant-detail-tab");

    setupDarkMode();

    // DARK MODE FUNCTIONALITY
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

    // Extract restaurant ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const restaurantId = urlParams.get("id");

    if (!restaurantId) {
        restaurantDetailContainer.innerHTML = '<div class="error">Restaurant ID not found.</div>';
        restaurantDetailTab.textContent = "ID Error";
        return;
    }

    // Fetch restaurant details from API using the ID
    fetch(`http://localhost:8080/api/restaurants/${restaurantId}`)
        .then((response) => {
            if (!response.ok) {
                throw new Error(`Network response was not ok. Status: ${response.status}`);
            }
            return response.json();
        })
        .then((restaurant) => {
            if (!restaurant) {
                restaurantDetailContainer.innerHTML = '<div class="error">Restaurant not found.</div>';
                restaurantDetailTab.textContent = "Not Found";
                return;
            }

            // Display restaurant details
            const name = restaurant.Name || restaurant.name || "Unnamed Restaurant";
            const type = restaurant.Category || restaurant.type || "Restaurant";
            const borough = restaurant.Borough || restaurant.borough || "";
            const imageUrl =
                restaurant.storePhoto ||
                "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg";

            document.title = `GoodEats - ${name}`;

            restaurantDetailContainer.innerHTML = `
                <h2>${name}</h2>
                <p>Type: ${type}</p>
                <p>Borough: ${borough}</p>
            `;
            restaurantDetailTab.innerHTML = `<a href="#">${name}</a>`;

            // Fetch and display reviews for this restaurant
            fetchReviews(restaurantId);
        })
        .catch((error) => {
            console.error("Error fetching restaurant details:", error);
            restaurantDetailContainer.innerHTML = `<div class="error">Error loading restaurant details. Please try again later.</div>`;
            restaurantDetailTab.textContent = "Fetch Error";
        });

    async function fetchReviews(restaurantId) {
        reviewsContainer.innerHTML = '<h3>Reviews:</h3><div class="loading">Loading reviews...</div>';
        try {
            const response = await fetch(`http://localhost:8080/api/restaurants/${restaurantId}/reviews`);
            if (!response.ok) {
                throw new Error(`Error fetching reviews. Status: ${response.status}`);
            }
            const reviews = await response.json();

            reviewsContainer.innerHTML = '<h3>Reviews:</h3>';

            if (Array.isArray(reviews) && reviews.length === 0) {
                reviewsContainer.innerHTML += "<p>No reviews yet.</p>";
                return;
            }

            for (const review of reviews) {
                const userResponse = await fetch(`http://localhost:8080/api/users/${review.userID}`);
                if (!userResponse.ok) {
                    console.error(`Error fetching user with ID ${review.userID}`);
                    continue;
                }
                const user = await userResponse.json();
                const username = (user && user.username) ? user.username : `User ${review.userID}`;

                const reviewCard = document.createElement("div");
                reviewCard.classList.add("review-card");

                const stars = generateStars(review.rating);

                reviewCard.innerHTML = `
                <p><strong>${username}</strong></p>
                <div class="restaurant-rating">
                    ${stars}
                </div>
                <p><strong>Review:</strong> ${review.review}</p>
                <p><small>Date: ${new Date(review.date).toLocaleDateString()}</small></p>
            `;
                reviewsContainer.appendChild(reviewCard);
            }
            setupDarkMode();
        } catch (error) {
            console.error("Error fetching reviews:", error);
            reviewsContainer.innerHTML = '<h3>Reviews:</h3><p>Error loading reviews. Please try again later.</p>';
        }
    }

    function generateStars(rating) {
        const starsContainer = document.createElement("div");
        starsContainer.classList.add("stars");
        starsContainer.dataset.rating = rating;

        for (let i = 1; i <= 5; i++) {
            const star = document.createElement("span");
            star.classList.add("star");
            if (i <= rating) {
                star.classList.add("active");
            }
            star.dataset.value = i;
            star.textContent = "★"; // Unicode star
            starsContainer.appendChild(star);
        }

        return starsContainer.outerHTML;
    }
});