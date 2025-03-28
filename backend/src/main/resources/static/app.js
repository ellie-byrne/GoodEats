document.addEventListener('DOMContentLoaded', () => {
    fetchRestaurants();
});

function fetchRestaurants() {
    const loading = document.getElementById('loading');
    const error = document.getElementById('error');
    const noResults = document.getElementById('no-results');
    const restaurantsGrid = document.getElementById('restaurants');

    loading.style.display = 'block';
    error.style.display = 'none';
    noResults.style.display = 'none';
    restaurantsGrid.innerHTML = '';

    fetch('/api/restaurants')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            loading.style.display = 'none';
            if (data.length === 0) {
                noResults.style.display = 'block';
            } else {
                data.forEach(restaurant => {
                    const card = document.createElement('div');
                    card.classList.add('restaurant-card');
                    card.innerHTML = `
                        <div class="restaurant-info">
                            <div class="restaurant-name">${restaurant.name}</div>
                        </div>
                    `;
                    restaurantsGrid.appendChild(card);
                });
            }
        })
        .catch(error => {
            loading.style.display = 'none';
            error.style.display = 'block';
            console.error('Error fetching restaurants:', error);
        });
}