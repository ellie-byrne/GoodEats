document.addEventListener("DOMContentLoaded", function() {
    //get restaurants from rest API
    fetch("/api/restaurants")
        .then(response => {
            if (!response.ok) {
                throw new Error("Response was not ok");
            }
            return response.json();
        })
        .then(restaurants => {
            console.log('Restuarants data:', restaurants);
            displayRestaurants(restaurants);
        })
        .catch(error => {
            console.error("Error fetching restaurants:", error);
            document.getElementById("restaurants-container").innerHTML =
                '<div class="error">Error loading restaurants.</div>';
        });
});

function displayRestaurants(restaurants) {
    const container = document.getElementById('restaurants-container');

    container.innerHTML = '';

    if (!restaurants || restaurants.length === 0) {
        container.innerHTML = '<div class="no-results">No restaurants found.</div>';
        return;
    }

    // Create a card for each restaurant
    restaurants.forEach(restaurant => {
        if (!restaurant.Name) return;

        const card = document.createElement('div');
        card.className = 'restaurant-card';
        const name = restaurant.Name || 'Unnamed Restaurant';
        const type = restaurant.Category || 'Restaurant';
        const borough = restaurant.Borough || 'London';
        const imageUrl = restaurant.storePhoto || 'https://via.placeholder.com/300x200?text=GoodEats';

        let reviewText = 'No review available';
        if (restaurant.review) {
            reviewText = restaurant.review.length > 100 ?
                restaurant.review.substring(0, 100) + '...' :
                restaurant.review;
        } else if (restaurant.reviews && restaurant.reviews.length > 0 && restaurant.reviews[0].body) {
            reviewText = restaurant.reviews[0].body.length > 100 ?
                restaurant.reviews[0].body.substring(0, 100) + '...' :
                restaurant.reviews[0].body;
        }

        card.innerHTML = `
            <img src="${imageUrl}" alt="${name}" class="restaurant-image" onerror="this.src='https://via.placeholder.com/300x200?text=GoodEats'">
            <div class="restaurant-info">
                <h2 class="restaurant-name">${name}</h2>
                <span class="restaurant-type">${type}</span>
                <p class="restaurant-review">${reviewText}</p>
                ${restaurant.link ? `<a href="${restaurant.link}" class="restaurant-link" target="_blank">Visit Website</a>` : ''}
            </div>
        `;

        container.appendChild(card);
    });
}