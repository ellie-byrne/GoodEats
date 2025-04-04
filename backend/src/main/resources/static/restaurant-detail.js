document.addEventListener("DOMContentLoaded", () => {
    const restaurantDetailContainer = document.getElementById("restaurant-detail-container");
    const restaurantDetailTab = document.getElementById("restaurant-detail-tab");

    setupDarkMode()

    // DARK MODE FUNCTIONALITY
    function setupDarkMode() {
        const darkModeToggle = document.getElementById("dark-mode-toggle");
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
                <img src="${imageUrl}" alt="${name}" onerror="this.src='https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg'">
                <h2>${name}</h2>
                <p>Type: ${type}</p>
                <p>Borough: ${borough}</p>
                ${restaurant.link ? `<p><a href="${restaurant.link}" target="_blank">Visit Website</a></p>` : ""}
                `;
            restaurantDetailTab.innerHTML = `<a href="#">${name}</a>`;
        })
        .catch((error) => {
            console.error("Error fetching restaurant details:", error);
            restaurantDetailContainer.innerHTML = `<div class="error">Error loading restaurant details. Please try again later.</div>`;
            restaurantDetailTab.textContent = "Fetch Error";
        });
});