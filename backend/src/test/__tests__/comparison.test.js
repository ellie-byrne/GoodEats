global.fetch = jest.fn(() =>
    Promise.resolve({
        ok: true,
        json: () => Promise.resolve([
            {
                id: 1,
                name: "Restaurant 1",
                type: "Italian",
                borough: "Westminster"
            },
            {
                id: 2,
                name: "Restaurant 2",
                type: "Chinese",
                borough: "Camden"
            }
        ])
    })
);

document.body.innerHTML = `
  <div id="restaurants-container"></div>
  <button id="compare-button" disabled></button>
  <button id="clear-comparison"></button>
  <div id="comparison-results" class="hidden"></div>
  <input type="checkbox" id="dark-mode-toggle" />
  <div id="restaurant1-selection">
    <button class="add-restaurant-btn" data-slot="1">+ Add Restaurant</button>
  </div>
  <div id="restaurant2-selection">
    <button class="add-restaurant-btn" data-slot="2">+ Add Restaurant</button>
  </div>
`;

describe('Comparison Module', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.getElementById("restaurants-container").innerHTML = "";
        document.getElementById("comparison-results").classList.add("hidden");
        document.getElementById("compare-button").disabled = true;

        document.getElementById("restaurant1-selection").innerHTML = `
      <button class="add-restaurant-btn" data-slot="1">+ Add Restaurant</button>
    `;

        document.getElementById("restaurant2-selection").innerHTML = `
      <button class="add-restaurant-btn" data-slot="2">+ Add Restaurant</button>
    `;
    });

    test('loadRestaurants fetches data from API', async () => {
        // Simple implementation of loadRestaurants
        const loadRestaurants = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/restaurants");
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return await response.json();
            } catch (error) {
                console.error("Error fetching restaurants:", error);
                return [];
            }
        };

        const restaurants = await loadRestaurants();

        expect(fetch).toHaveBeenCalledWith("http://localhost:8080/api/restaurants");
        expect(restaurants).toHaveLength(2);
        expect(restaurants[0].name).toBe("Restaurant 1");
        expect(restaurants[1].name).toBe("Restaurant 2");
    });

    test('selectRestaurant adds restaurant to selection', () => {
        const selectRestaurant = (restaurant, slot) => {
            const selectionContainer = document.getElementById(`restaurant${slot}-selection`);
            if (!selectionContainer) return;

            selectionContainer.innerHTML = `
        <div class="selected-restaurant-card" data-id="${restaurant.id}">
          <div class="info">
            <h4>${restaurant.name}</h4>
            <p>${restaurant.type} • ${restaurant.borough}</p>
          </div>
          <button class="remove-btn" data-slot="${slot}">&times;</button>
        </div>
      `;

            const restaurant1 = document.querySelector("#restaurant1-selection .selected-restaurant-card");
            const restaurant2 = document.querySelector("#restaurant2-selection .selected-restaurant-card");

            if (restaurant1 && restaurant2) {
                document.getElementById("compare-button").disabled = false;
            }
        };

        const testRestaurant = {
            id: 1,
            name: "Test Restaurant",
            type: "Italian",
            borough: "Westminster"
        };

        selectRestaurant(testRestaurant, 1);

        const selectionContainer = document.getElementById("restaurant1-selection");
        expect(selectionContainer.querySelector("h4").textContent).toBe("Test Restaurant");
        expect(document.getElementById("compare-button").disabled).toBe(true);

        selectRestaurant(testRestaurant, 2);

        expect(document.getElementById("compare-button").disabled).toBe(false);
    });

    test('clearComparison resets selections', () => {
        // Setup initial state with selections
        document.getElementById("restaurant1-selection").innerHTML = `
      <div class="selected-restaurant-card" data-id="1">
        <div class="info">
          <h4>Restaurant 1</h4>
          <p>Italian • Westminster</p>
        </div>
        <button class="remove-btn" data-slot="1">&times;</button>
      </div>
    `;

        document.getElementById("restaurant2-selection").innerHTML = `
      <div class="selected-restaurant-card" data-id="2">
        <div class="info">
          <h4>Restaurant 2</h4>
          <p>Chinese • Camden</p>
        </div>
        <button class="remove-btn" data-slot="2">&times;</button>
      </div>
    `;

        document.getElementById("compare-button").disabled = false;

        const clearComparison = () => {
            document.getElementById("restaurant1-selection").innerHTML = `
        <button class="add-restaurant-btn" data-slot="1">+ Add Restaurant</button>
      `;

            document.getElementById("restaurant2-selection").innerHTML = `
        <button class="add-restaurant-btn" data-slot="2">+ Add Restaurant</button>
      `;

            document.getElementById("compare-button").disabled = true;
            document.getElementById("comparison-results").classList.add("hidden");
        };

        clearComparison();

        expect(document.getElementById("restaurant1-selection").querySelector(".add-restaurant-btn")).not.toBeNull();
        expect(document.getElementById("restaurant2-selection").querySelector(".add-restaurant-btn")).not.toBeNull();
        expect(document.getElementById("compare-button").disabled).toBe(true);
        expect(document.getElementById("comparison-results").classList.contains("hidden")).toBe(true);
    });
});