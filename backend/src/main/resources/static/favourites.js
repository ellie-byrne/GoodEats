document.addEventListener("DOMContentLoaded", () => {
    const favouritesContainer = document.getElementById("favourites-container")

    // Get favourites from localStorage
    const favourites = JSON.parse(localStorage.getItem("goodEatsfavourites")) || []

    if (favourites.length === 0) {
        // If no favourites, show the default message (already in HTML)
        return
    }

    // Clear the no-favourites message
    favouritesContainer.innerHTML = ""

    // Display the favourite restaurants
    favourites.forEach((restaurant) => {
        const card = createRestaurantCard(restaurant)
        favouritesContainer.appendChild(card)
    })
})

function createRestaurantCard(restaurant) {
    const card = document.createElement("div")
    card.className = "restaurant-card"

    // Use default values if properties are missing
    const name = restaurant.Name || restaurant.name || "Unnamed Restaurant"
    const type = restaurant.Category || restaurant.type || "Restaurant"
    const borough = restaurant.Borough || ""
    const imageUrl =
        restaurant.storePhoto ||
        "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg"

    // Create HTML structure for the card
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
            <button class="favourite-button active" data-id="${restaurant.id || restaurant._id}">★</button>
        </div>
    `

    // Add event listener to the favourite button
    const favouriteButton = card.querySelector(".favourite-button")
    favouriteButton.addEventListener("click", () => {
        const favouritesContainer = document.getElementById("favourites-container") // Declare favouritesContainer here
        removeFromfavourites(restaurant)
        card.remove()

        // If no favourites left, show the default message
        const remainingCards = document.querySelectorAll(".restaurant-card")
        if (remainingCards.length === 0) {
            favouritesContainer.innerHTML = `
                <div class="no-favourites">
                    <h2>Your favourites</h2>
                    <p>You haven't added any favourite restaurants yet.</p>
                    <a href="index.html" class="button">Browse Restaurants</a>
                </div>
            `
        }
    })

    return card
}

function removeFromfavourites(restaurant) {
    const favourites = JSON.parse(localStorage.getItem("goodEatsfavourites")) || []
    const restaurantId = restaurant.id || restaurant._id

    const updatedfavourites = favourites.filter((fav) => {
        const favId = fav.id || fav._id
        return favId !== restaurantId
    })

    localStorage.setItem("goodEatsfavourites", JSON.stringify(updatedfavourites))
}

