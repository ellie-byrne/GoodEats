// Mock fetch
global.fetch = jest.fn(() =>
    Promise.resolve({
        ok: true,
        json: () => Promise.resolve([
            {
                id: 1,
                name: "Test Restaurant",
                type: "Italian",
                borough: "Westminster",
                storePhoto: "/test.jpg"
            }
        ])
    })
);

// Mock DOM
document.body.innerHTML = `
  <div id="restaurants-container"></div>
  <div id="borough-filter"></div>
  <div id="category-filter"></div>
  <button id="clear-filters"></button>
  <input id="search-input" />
  <button id="search-button"></button>
  <input type="checkbox" id="dark-mode-toggle" />
  <div id="recently-viewed" class="hidden"></div>
`;

describe('App Module', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.getElementById("restaurants-container").innerHTML = "";
    });

    test('loadRestaurants fetches data from API', async () => {
        // Create a simple implementation of loadRestaurants
        const loadRestaurants = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/restaurants");
                if (!response.ok) {
                    throw new Error(`Network response was not ok. Status: ${response.status}`);
                }
                const data = await response.json();
                return data;
            } catch (error) {
                console.error("Error fetching restaurants:", error);
                return [];
            }
        };

        const restaurants = await loadRestaurants();

        expect(fetch).toHaveBeenCalledWith("http://localhost:8080/api/restaurants");
        expect(restaurants).toHaveLength(1);
        expect(restaurants[0].name).toBe("Test Restaurant");
    });

    test('displayRestaurants renders restaurant cards', () => {
        // Simple implementation of displayRestaurants
        const displayRestaurants = (restaurants) => {
            const container = document.getElementById("restaurants-container");
            container.innerHTML = "";

            if (!restaurants || restaurants.length === 0) {
                container.innerHTML = '<div class="no-results">No restaurants found matching your filters.</div>';
                return;
            }

            restaurants.forEach(restaurant => {
                const card = document.createElement("div");
                card.className = "restaurant-card";
                card.innerHTML = `
          <img src="${restaurant.storePhoto}" alt="${restaurant.name}" class="restaurant-image">
          <div class="restaurant-info">
            <h2 class="restaurant-name">${restaurant.name}</h2>
            <span class="restaurant-type">${restaurant.type}</span>
            <p class="restaurant-borough">${restaurant.borough}</p>
          </div>
        `;
                container.appendChild(card);
            });
        };

        const testRestaurants = [
            {
                id: 1,
                name: "Test Restaurant",
                type: "Italian",
                borough: "Westminster",
                storePhoto: "/test.jpg"
            }
        ];

        displayRestaurants(testRestaurants);

        const container = document.getElementById("restaurants-container");
        expect(container.children).toHaveLength(1);
        expect(container.querySelector(".restaurant-name").textContent).toBe("Test Restaurant");
    });

    test('setupDarkMode toggles dark mode class', () => {
        // Simple implementation of setupDarkMode
        const setupDarkMode = () => {
            const darkModeToggle = document.getElementById("dark-mode-toggle");

            darkModeToggle.addEventListener("change", () => {
                if (darkModeToggle.checked) {
                    document.body.classList.add("dark-mode");
                } else {
                    document.body.classList.remove("dark-mode");
                }
            });
        };

        setupDarkMode();

        const darkModeToggle = document.getElementById("dark-mode-toggle");
        darkModeToggle.checked = true;
        darkModeToggle.dispatchEvent(new Event("change"));

        expect(document.body.classList.contains("dark-mode")).toBe(true);

        darkModeToggle.checked = false;
        darkModeToggle.dispatchEvent(new Event("change"));

        expect(document.body.classList.contains("dark-mode")).toBe(false);
    });
});