document.addEventListener("DOMContentLoaded", () => {
    // DOM elements
    const container = document.getElementById("restaurants-container")
    const boroughFilter = document.getElementById("borough-filter")
    const categoryFilter = document.getElementById("category-filter")
    const clearFiltersButton = document.getElementById("clear-filters")
    const searchInput = document.getElementById("search-input")
    const searchButton = document.getElementById("search-button")
    const listViewBtn = document.getElementById("list-view-btn")
    const mapViewBtn = document.getElementById("map-view-btn")
    const mapContainer = document.getElementById("map-container")
    const restaurantsContainer = document.getElementById("restaurants-container")
    const darkModeToggle = document.getElementById("dark-mode-toggle")
    const recentlyViewedSection = document.getElementById("recently-viewed")

    // Store all restaurants for filtering
    let allRestaurants = []

    // Fetch restaurants from API
    fetch("http://localhost:8080/api/restaurants")
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

            // Initialize all features
            populateFilters(restaurants)
            displayRestaurants(restaurants)
            setupDarkMode()
            setupSearch()
            setupRatings()
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
    function displayRestaurants(restaurants) {
        const favourites = JSON.parse(localStorage.getItem("goodEatsFavourites")) || []

        const favouriteIds = favourites.reduce((map, restaurant) => {
            const id = restaurant.id || restaurant._id
            if (id) map[id] = true
            return map
        }, {})

        container.innerHTML = ""

        if (!restaurants || restaurants.length === 0) {
            container.innerHTML = '<div class="no-results">No restaurants found matching your filters.</div>'
            return
        }

        restaurants.forEach((restaurant) => {
            const name = restaurant.Name || restaurant.name || "Unnamed Restaurant"
            const type = restaurant.Category || restaurant.type || "Restaurant"
            const borough = restaurant.Borough || restaurant.borough || ""

            const imageUrl =
                restaurant.storePhoto ||
                "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg"

            const restaurantId = restaurant.id || restaurant._id
            const isFavourite = favouriteIds[restaurantId] || false

            // Create the card element
            const card = document.createElement("div")
            card.className = "restaurant-card"

            card.innerHTML = `
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
            `

            const favouriteButton = card.querySelector(".favourite-button")
            favouriteButton.addEventListener("click", (e) => {
                e.stopPropagation() // Prevent card click
                toggleFavourite(restaurant, favouriteButton)
            })

            // Add click event to the card for recently viewed
            card.addEventListener("click", (e) => {
                if (e.target.closest(".favourite-button") || e.target.closest(".star")) {
                    return
                }
                addToRecentlyViewed(restaurant)

                const restaurantId = restaurant.id
                window.location.href = `http://localhost:8080/restaurant-detail.html?id=${restaurantId}`
            })

            container.appendChild(card)
        })
        setupRatings()
    }

    // FAVOURITE FUNCTIONALITY
    function toggleFavourite(restaurant, button) {
        const favourites = JSON.parse(localStorage.getItem("goodEatsFavourites")) || []
        const restaurantId = restaurant.id || restaurant._id

        const existingIndex = favourites.findIndex((fav) => {
            const favId = fav.id || fav._id
            return favId === restaurantId
        })

        if (existingIndex >= 0) {
            favourites.splice(existingIndex, 1)
            button.classList.remove("active")
        } else {
            favourites.push(restaurant)
            button.classList.add("active")
        }

        localStorage.setItem("goodEatsFavourites", JSON.stringify(favourites))
    }

    // RATING FUNCTIONALITY
    function setupRatings() {
        // Load existing ratings from localStorage
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

        // Update or create rating for this restaurant
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

        // Update UI
        updateStars(starsContainer, ratings[restaurantId].averageRating)
        const count = ratings[restaurantId].count
        ratingCountElement.textContent = `(${count} rating${count !== 1 ? "s" : ""})`

        // Show confirmation message
        showRatingConfirmation(rating)
    }

    function showRatingConfirmation(rating) {
        const message = document.createElement("div")
        message.className = "rating-confirmation"
        message.textContent = `Thanks for rating ${rating} stars!`

        message.style.position = "fixed"
        message.style.bottom = "20px"
        message.style.right = "20px"
        message.style.backgroundColor = "#ff6b6b"
        message.style.color = "white"
        message.style.padding = "10px 20px"
        message.style.borderRadius = "4px"
        message.style.boxShadow = "0 2px 10px rgba(0, 0, 0, 0.2)"
        message.style.zIndex = "1000"

        document.body.appendChild(message)

        setTimeout(() => {
            message.style.opacity = "0"
            message.style.transition = "opacity 0.5s"
            setTimeout(() => {
                document.body.removeChild(message)
            }, 500)
        }, 3000)
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
            const imageUrl =
                restaurant.storePhoto ||
                "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg"

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
                    mainCard.scrollIntoView({ behavior: "smooth" })
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

