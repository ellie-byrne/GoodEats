document.addEventListener("DOMContentLoaded", () => {
    const container = document.getElementById("restaurants-container")

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

            displayRestaurants(restaurants)
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
})

function displayRestaurants(restaurants) {
    const container = document.getElementById("restaurants-container")

    // Get favourites from localStorage
    const favourites = JSON.parse(localStorage.getItem("goodEatsfavourites")) || []

    const favouriteIds = favourites.reduce((map, restaurant) => {
        const id = restaurant.id || restaurant._id
        if (id) map[id] = true
        return map
    }, {})

    container.innerHTML = ""

    if (!restaurants || restaurants.length === 0) {
        container.innerHTML = '<div class="no-results">No restaurants found.</div>'
        return
    }

    if (restaurants.length > 0) {
        console.log("First restaurant details:", JSON.stringify(restaurants[0], null, 2))
    }

    restaurants.forEach((restaurant, index) => {
        console.log(`Processing restaurant ${index}:`, restaurant)

        const name = restaurant.Name || restaurant.name || "Unnamed Restaurant"

        const card = document.createElement("div")
        card.className = "restaurant-card"

        const type = restaurant.Category || restaurant.type || "Restaurant"

        const borough = restaurant.Borough || ""

        const imageUrl =
            restaurant.storePhoto ||
            "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg"

        const restaurantId = restaurant.id || restaurant._id
        const isfavourite = favouriteIds[restaurantId] || false

        card.innerHTML = `
            <img src="${imageUrl}" alt="${name}" class="restaurant-image" 
                onerror="this.src='https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg'">
            <div class="restaurant-info">
                <h2 class="restaurant-name">${name}</h2>
                <span class="restaurant-type">${type}</span>
                ${borough ? `<p class="restaurant-borough">${borough}</p>` : ""}
                ${
            restaurant.link
                ? `<a href="${restaurant.link}" class="restaurant-link" target="_blank">Visit Website</a>`
                : ""
        }
                <button class="favourite-button ${isfavourite ? "active" : ""}" data-id="${restaurantId}">★</button>
            </div>
        `

        const favouriteButton = card.querySelector(".favourite-button")
        favouriteButton.addEventListener("click", () => {
            togglefavourite(restaurant, favouriteButton)
        })

        container.appendChild(card)
    })
}

function togglefavourite(restaurant, button) {
    const favourites = JSON.parse(localStorage.getItem("goodEatsfavourites")) || []
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

    localStorage.setItem("goodEatsfavourites", JSON.stringify(favourites))
}

