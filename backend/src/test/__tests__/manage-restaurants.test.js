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

document.body.innerHTML = `
  <div id="restaurants-container"></div>
  <form id="add-restaurant-form">
    <input id="name" value="New Restaurant" />
    <input id="type" value="Chinese" />
    <select id="borough"><option value="Camden">Camden</option></select>
    <input id="storePhoto" value="" />
  </form>
  <input type="checkbox" id="dark-mode-toggle" />
`;

describe('Manage Restaurants Module', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.getElementById("restaurants-container").innerHTML = "";
    });

    test('loadRestaurants fetches data from API', async () => {
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
        expect(restaurants).toHaveLength(1);
        expect(restaurants[0].name).toBe("Test Restaurant");
    });

    test('displayRestaurants renders restaurant items', () => {
        const displayRestaurants = (restaurants) => {
            const container = document.getElementById("restaurants-container");
            container.innerHTML = '';

            if (!restaurants || restaurants.length === 0) {
                container.innerHTML = '<div class="no-results">No restaurant found.</div>';
                return;
            }

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

                container.appendChild(restaurantItem);
            });
        };

        const testRestaurants = [
            {
                id: 1,
                name: "Test Restaurant",
                type: "Italian",
                borough: "Westminster"
            }
        ];

        displayRestaurants(testRestaurants);

        const container = document.getElementById("restaurants-container");
        expect(container.children).toHaveLength(1);
        expect(container.querySelector(".restaurant-name").textContent).toBe("Test Restaurant");
        expect(container.querySelector(".delete-btn").dataset.id).toBe("1");
    });

    test('addRestaurant sends POST request with form data', async () => {
        fetch.mockImplementationOnce(() =>
            Promise.resolve({
                ok: true,
                json: () => Promise.resolve({ id: 2, name: "New Restaurant" })
            })
        );

        const addRestaurant = async () => {
            const name = document.getElementById("name").value;
            const type = document.getElementById("type").value;
            const borough = document.getElementById("borough").value;
            const storePhoto = document.getElementById("storePhoto").value ||
                "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg";

            const newRestaurant = {
                name: name,
                type: type,
                Borough: borough,
                storePhoto: storePhoto
            };

            try {
                const response = await fetch("http://localhost:8080/api/restaurants", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(newRestaurant)
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }

                return await response.json();
            } catch (error) {
                console.error("Error adding restaurant:", error);
                return null;
            }
        };

        const result = await addRestaurant();

        expect(fetch).toHaveBeenCalledWith(
            "http://localhost:8080/api/restaurants",
            expect.objectContaining({
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name: "New Restaurant",
                    type: "Chinese",
                    Borough: "Camden",
                    storePhoto: "https://marketplace.canva.com/EAFpeiTrl4c/2/0/400w/canva-abstract-chef-cooking-restaurant-free-logo-w0RUdbkI0xE.jpg"
                })
            })
        );

        expect(result).not.toBeNull();
        expect(result.name).toBe("New Restaurant");
    });
});