document.addEventListener("DOMContentLoaded", () => {
    const favouritesContainer = document.getElementById("favourites-container");
    const darkModeToggle = document.getElementById("dark-mode-toggle");

    setupDarkMode(darkModeToggle); // Pass the toggle element to the function

    // Retrieve favorites from local storage
    const favourites = JSON.parse(localStorage.getItem("goodEatsFavourites")) || [];
    console.log("Favourites loaded:", favourites); // Debugging line

    // Check if there are any favourites
    if (favourites.length === 0) {
        favouritesContainer.innerHTML = `
            <div class="no-favourites">
                <h2>Your Favourites</h2>
                <p>You haven't added any favourite restaurants yet.</p>
                <a href="index.html" class="button">Browse Restaurants</a>
            </div>
        `;
        return; // Exit if no favourites
    }

    // Clear the container before adding new cards
    favouritesContainer.innerHTML = "";

    // Create and append restaurant cards for each favourite
    favourites.forEach((restaurant) => {
        const card = createRestaurantCard(restaurant);
        favouritesContainer.appendChild(card);
    });

    // Setup ratings after cards are created
    setupRatings();
});

// DARK MODE FUNCTIONALITY
function setupDarkMode(darkModeToggle) { // Accept darkModeToggle as an argument
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

// RATING FUNCTIONALITY
function setupRatings() {
    const ratings = JSON.parse(localStorage.getItem("goodEatsRatings")) || {}

    // Update restaurant cards with ratings
    document.querySelectorAll(".restaurant-card").forEach((card) => {
        const restaurantId = card.querySelector(".favourite-button").dataset.id
        const starsContainer = card.querySelector(".stars")
        const ratingCount = card.querySelector(".rating-count")

        if (ratings[restaurantId]) {
            const { averageRating, count } = ratings[restaurantId]
            updateStars(starsContainer, averageRating)
            ratingCount.textContent = `(${count} rating${count !== 1 ? "s" : ""})`
        }

        // Add event listeners to stars
        card.querySelectorAll(".star").forEach((star) => {
            star.addEventListener("click", (e) => {
                e.stopPropagation() // Prevent card click
                const value = Number.parseInt(star.dataset.value)
                rateRestaurant(restaurantId, value, starsContainer, ratingCount)
            })
        })
    })
}

function updateStars(starsContainer, rating) {
    starsContainer.dataset.rating = rating

    const stars = starsContainer.querySelectorAll(".star")
    stars.forEach((star) => {
        const value = Number.parseInt(star.dataset.value)
        if (value <= rating) {
            star.classList.add("active")
        } else {
            star.classList.remove("active")
        }
    })
}

function rateRestaurant(restaurantId, rating, starsContainer, ratingCountElement) {
    // Get existing ratings
    const ratings = JSON.parse(localStorage.getItem("goodEatsRatings")) || {}

    if (!ratings[restaurantId]) {
        ratings[restaurantId] = {
            totalRating: rating,
            count: 1,
            averageRating: rating,
        }
    } else {
        ratings[restaurantId].totalRating += rating
        ratings[restaurantId].count += 1
        ratings[restaurantId].averageRating = ratings[restaurantId].totalRating / ratings[restaurantId].count
    }

    // Save updated ratings
    localStorage.setItem("goodEatsRatings", JSON.stringify(ratings))

    updateStars(starsContainer, ratings[restaurantId].averageRating)
    const count = ratings[restaurantId].count
    ratingCountElement.textContent = `(${count} rating${count !== 1 ? "s" : ""})`

    showRatingConfirmation(rating)
}

function showRatingConfirmation(rating) {
    // Create a temporary message
    const message = document.createElement("div")
    message.className = "rating-confirmation"
    message.textContent = `Thanks for rating ${rating} stars!`

    // Style the message
    message.style.position = "fixed"
    message.style.bottom = "20px"
    message.style.right = "20px"
    message.style.backgroundColor = "#ff6b6b"
    message.style.color = "white"
    message.style.padding = "10px 20px"
    message.style.borderRadius = "4px"
    message.style.boxShadow = "0 2px 10px rgba(0, 0, 0, 0.2)"
    message.style.zIndex = "1000"

    // Add to document
    document.body.appendChild(message)

    // Remove after 3 seconds
    setTimeout(() => {
        message.style.opacity = "0"
        message.style.transition = "opacity 0.5s"
        setTimeout(() => {
            document.body.removeChild(message)
        }, 500)
    }, 3000)
}

// Function to create restaurant card
function createRestaurantCard(restaurant) {
    const card = document.createElement("div");
    card.className = "restaurant-card";

    // Use default values if properties are missing
    const name = restaurant.name || restaurant.Name || "Unnamed Restaurant";
    const type = restaurant.category || restaurant.type || restaurant.Category || "Restaurant";
    const borough = restaurant.borough || restaurant.Borough || "";
    const imageUrl =
        restaurant.storePhoto || "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg";

    // Create a new Image object to preload
    const img = new Image();
    img.src = imageUrl;

    // Create HTML structure for the card
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
            <button class="favourite-button active" data-id="${restaurant.id || restaurant._id}">★</button>
        </div>
    `;

    // Add event listener to the favorite button
    const favouriteButton = card.querySelector(".favourite-button");
    favouriteButton.addEventListener("click", () => {
        removeFromFavourites(restaurant);
        card.remove();

        // If no favourites left, show the default message
        const remainingCards = document.querySelectorAll(".restaurant-card");
        if (remainingCards.length === 0) {
            favouritesContainer.innerHTML = `
                <div class="no-favourites">
                    <h2>Your Favourites</h2>
                    <p>You haven't added any favourite restaurants yet.</p>
                    <a href="index.html" class="button">Browse Restaurants</a>
                </div>
            `;
        }
    });

    return card;
}

// Function to remove from favourites
function removeFromFavourites(restaurant) {
    const favourites = JSON.parse(localStorage.getItem("goodEatsFavourites")) || [];
    const restaurantId = restaurant.id || restaurant._id;

    const updatedFavourites = favourites.filter((fav) => {
        const favId = fav.id || fav._id;
        return favId !== restaurantId;
    });

    localStorage.setItem("goodEatsFavourites", JSON.stringify(updatedFavourites));
}