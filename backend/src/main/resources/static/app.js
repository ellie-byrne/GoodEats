document.addEventListener("DOMContentLoaded", () => {
    // Navigation
    const homeLink = document.getElementById("home-link")
    const addRestaurantLink = document.getElementById("add-restaurant-link")
    const homeContent = document.getElementById("home-content")
    const addRestaurantContent = document.getElementById("add-restaurant-content")

    // Form
    const addRestaurantForm = document.getElementById("add-restaurant-form")

    // Navigation event listeners
    homeLink.addEventListener("click", (e) => {
        e.preventDefault()
        homeContent.classList.remove("hidden")
        addRestaurantContent.classList.add("hidden")
        homeLink.classList.add("active")
        addRestaurantLink.classList.remove("active")

        // Reload restaurants when returning to home
        loadRestaurants()
    })

    addRestaurantLink.addEventListener("click", (e) => {
        e.preventDefault()
        homeContent.classList.add("hidden")
        addRestaurantContent.classList.remove("hidden")
        homeLink.classList.remove("active")
        addRestaurantLink.classList.add("active")
    })

    // Form submission
    addRestaurantForm.addEventListener("submit", (e) => {
        e.preventDefault()

        // Remove any existing messages
        const existingMessages = document.querySelectorAll(".success-message, .error-message")
        existingMessages.forEach((msg) => msg.remove())

        // Get form data
        const formData = {
            name: document.getElementById("restaurant-name").value,
            category: document.getElementById("restaurant-category").value,
            borough: document.getElementById("restaurant-borough").value,
            link: document.getElementById("restaurant-link").value,
        }

        // Submit the data
        fetch("/api/restaurants", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(formData),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Failed to add restaurant")
                }
                return response.json()
            })
            .then((data) => {
                // Show success message
                const successMessage = document.createElement("div")
                successMessage.className = "success-message"
                successMessage.textContent = "Restaurant added successfully!"
                addRestaurantForm.prepend(successMessage)

                // Reset the form
                addRestaurantForm.reset()

                // Auto-hide message after 3 seconds
                setTimeout(() => {
                    successMessage.remove()
                }, 3000)
            })
            .catch((error) => {
                // Show error message
                const errorMessage = document.createElement("div")
                errorMessage.className = "error-message"
                errorMessage.textContent = "Error adding restaurant. Please try again."
                addRestaurantForm.prepend(errorMessage)

                console.error("Error adding restaurant:", error)
            })
    })

    // Load restaurants on page load
    loadRestaurants()
})

function loadRestaurants() {
    const container = document.getElementById("restaurants-container")
    container.innerHTML = '<div class="loading">Loading restaurants...</div>'

    fetch("/api/restaurants")
        .then((response) => {
            if (!response.ok) {
                throw new Error("Response was not ok")
            }
            return response.json()
        })
        .then((restaurants) => {
            console.log("Restaurants data:", restaurants)
            displayRestaurants(restaurants)
        })
        .catch((error) => {
            console.error("Error fetching restaurants:", error)
            container.innerHTML = '<div class="error">Error loading restaurants.</div>'
        })
}

function displayRestaurants(restaurants) {
    const container = document.getElementById("restaurants-container")

    container.innerHTML = ""

    if (!restaurants || restaurants.length === 0) {
        container.innerHTML = '<div class="no-results">No restaurants found.</div>'
        return
    }

    restaurants.forEach((restaurant) => {
        const card = document.createElement("div")
        card.className = "restaurant-card"
        const name = restaurant.name || "Unnamed Restaurant"
        const category = restaurant.category || "Restaurant"
        const borough = restaurant.borough || "London"
        const imageUrl = restaurant.storePhoto || "/placeholder.svg?height=300&width=400&text=" + name
        const link =
            restaurant.link || `https://www.google.com/search?q=${encodeURIComponent(name + " " + borough + " London")}`

        card.innerHTML = `
            <img src="${imageUrl}" alt="${name}" class="restaurant-image" onerror="this.src='/placeholder.svg?height=300&width=400&text=${name}'">
            <div class="restaurant-info">
                <h2 class="restaurant-name">${name}</h2>
                <span class="restaurant-type">${category}</span>
                <div class="restaurant-location">${borough}</div>
                <a href="${link}" class="restaurant-link" target="_blank">Visit Website</a>
            </div>
        `

        container.appendChild(card)
    })
}

