document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('restaurants-container');

    // Fetch restaurants from the API
    fetch('http://localhost:8080/api/restaurants')
        .then(response => {
            console.log('Response status:', response.status);

            if (!response.ok) {
                throw new Error(`Network response was not ok. Status: ${response.status}`);
            }
            return response.json();
        })
        .then(restaurants => {
            console.log('Restaurants data:', restaurants);

            // Basic data validation
            if (!Array.isArray(restaurants)) {
                console.error('Expected an array of restaurants but got:', typeof restaurants);
                container.innerHTML = '<div class="error">Invalid data format received from server.</div>';
                return;
            }

            displayRestaurants(restaurants);
        })
        .catch(error => {
            console.error('Error fetching restaurants:', error);

            // Detailed error message
            container.innerHTML = `
                <div class="error">
                    <p>Error loading restaurants. Please try again later.</p>
                    <p class="error-details">${error.message}</p>
                    <button onclick="location.reload()">Retry</button>
                </div>
            `;
        });
});

function displayRestaurants(restaurants) {
    const container = document.getElementById('restaurants-container');

    // Clear loading message
    container.innerHTML = '';

    if (!restaurants || restaurants.length === 0) {
        container.innerHTML = '<div class="no-results">No restaurants found.</div>';
        return;
    }

    // Log the first restaurant for debugging
    if (restaurants.length > 0) {
        console.log('First restaurant details:', JSON.stringify(restaurants[0], null, 2));
    }

    // Create a card for each restaurant
    restaurants.forEach((restaurant, index) => {
        console.log(`Processing restaurant ${index}:`, restaurant);

        // Use Name field if available, otherwise fall back to name
        const name = restaurant.Name || restaurant.name || 'Unnamed Restaurant';

        const card = document.createElement('div');
        card.className = 'restaurant-card';

        // Use Category if available, otherwise fall back to type
        const type = restaurant.Category || restaurant.type || 'Restaurant';

        // Use Borough if available
        const borough = restaurant.Borough || '';

        // Use storePhoto if available, otherwise use placeholder
        const imageUrl = restaurant.storePhoto || 'https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg';

        // Create HTML structure for the card
        card.innerHTML = `
            <img src="${imageUrl}" alt="${name}" class="restaurant-image" 
                onerror="this.src='https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg'">
            <div class="restaurant-info">
                <h2 class="restaurant-name">${name}</h2>
                <span class="restaurant-type">${type}</span>
                ${borough ? `<p class="restaurant-borough">${borough}</p>` : ''}
                ${restaurant.link ?
            `<a href="${restaurant.link}" class="restaurant-link" target="_blank">Visit Website</a>` : ''}
            </div>
        `;

        container.appendChild(card);
    });
}