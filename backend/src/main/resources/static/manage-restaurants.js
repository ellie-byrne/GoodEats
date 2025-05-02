document.addEventListener("DOMContentLoaded", () => {
    const restaurantsContainer = document.getElementById("restaurants-container");
    const addRestaurantForm = document.getElementById("add-restaurant-form");
    const darkModeToggle = document.getElementById("dark-mode-toggle");
    setupDarkMode();
    loadRestaurants();
    addRestaurantForm.addEventListener("submit", (e) => {
        e.preventDefault();
        addRestaurant();
    });
    function loadRestaurants() {
        restaurantsContainer.innerHTML = '<div class="loading">Loading restaurants...</div>';
        fetch("http://34.142.84.120:5000/api/restaurants")
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(restaurants => {
                displayRestaurants(restaurants);
                populateBoroughs(restaurants);

            })
            .catch(error => {
                console.error("Error fetching restaurants:", error);
                restaurantsContainer.innerHTML = `
                    <div class="error">
                        <p>Error loading restaurants. Please try again later.</p>
                        <p class="error-details">${error.message}</p>
                        <button onclick="loadRestaurants()">Retry</button>
                    </div>
                `;
            });
    }

    function populateBoroughs(restaurants) {
        const boroughSelect = document.getElementById("borough");
        const boroughSet = new Set();

        restaurants.forEach(r => {
            const borough = r.borough || r.Borough;
            if (borough && borough.trim() !== "") {
                boroughSet.add(borough);
            }
        });

        const sorted = Array.from(boroughSet).sort();
        boroughSelect.innerHTML = '<option value="">Select Borough</option>';

        sorted.forEach(b => {
            const option = document.createElement("option");
            option.value = b;
            option.textContent = b;
            boroughSelect.appendChild(option);
        });
    }

    function displayRestaurants(restaurants) {
        if (!restaurants || restaurants.length === 0) {
            restaurantsContainer.innerHTML = '<div class="no-results">No restaurant found.</div>';
            return;
        }
        restaurantsContainer.innerHTML = '';
        restaurants.sort((a, b) => {
            const nameA = (a.name || a.Name || "").toLowerCase();
            const nameB = (b.name || b.Name || "").toLowerCase();
            return nameA.localeCompare(nameB);
        });
        restaurants.forEach(restaurant => {
            const name = restaurant.name || restaurant.Name || "Unnamed Restaurant";
            const type = restaurant.type || restaurant.Category || "Restaurant";
            const borough = restaurant.Borough ?? restaurant.borough ?? "";
            const id = restaurant.id || restaurant._id;
            const restaurantItem = document.createElement("div");
            restaurantItem.className = "restaurant-item";
            restaurantItem.innerHTML = `
                <div class="restaurant-info">
                    <div class="restaurant-name">${name}</div>
                    <div class="restaurant-details">${type} • ${borough}</div>
                </div>
                <div class="restaurant-actions">
                    <button class="delete-btn" data-id="${id}">Delete</button>
                </div>
            `;

            restaurantsContainer.appendChild(restaurantItem);
            const deleteBtn = restaurantItem.querySelector(".delete-btn");
            deleteBtn.addEventListener("click", () => {
                if (confirm(`Are you sure you want to delete "${name}"?`)) {
                    deleteRestaurant(id);
                }
            });
        });
    }

    function addRestaurant() {
        const name = document.getElementById("name").value;
        const type = document.getElementById("type").value;
        const borough = document.getElementById("borough").value;
        const storePhoto = document.getElementById("storePhoto").value ||
            "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg";
        const newRestaurant = {
            name: name,
            type: type,
            Borough: borough,
            storePhoto: storePhoto,
            reviews: []
        };
        fetch("http://34.142.84.120:5000/api/restaurants", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(newRestaurant)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                addRestaurantForm.reset();

                alert(`Restaurant "${name}" added successfully!`);
                loadRestaurants();
            })
            .catch(error => {
                console.error("Error adding restaurant:", error);
                alert(`Error adding restaurant: ${error.message}`);
            });
    }

    function deleteRestaurant(id) {
        fetch(`http://34.142.84.120:5000/api/restaurants/${id}`, {
            method: "DELETE"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                loadRestaurants();
                alert("Restaurant deleted successfully!");
            })
            .catch(error => {
                console.error("Error deleting restaurant:", error);
                alert(`Error deleting restaurant: ${error.message}`);
            });
    }
    function setupDarkMode() {
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
});