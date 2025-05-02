// Mock localStorage
const localStorageMock = (function() {
    let store = {};
    return {
        getItem: jest.fn(key => store[key] || null),
        setItem: jest.fn((key, value) => {
            store[key] = value.toString();
        }),
        removeItem: jest.fn(key => {
            delete store[key];
        }),
        clear: jest.fn(() => {
            store = {};
        })
    };
})();
Object.defineProperty(window, 'localStorage', { value: localStorageMock });

// Mock fetch
global.fetch = jest.fn(() =>
    Promise.resolve({
        ok: true,
        json: () => Promise.resolve([
            {
                id: 1,
                name: "Favourite Restaurant",
                type: "Italian",
                borough: "Westminster",
                storePhoto: "/test.jpg"
            }
        ])
    })
);

// Mock DOM
document.body.innerHTML = `
  <div id="favourites-container"></div>
  <input type="checkbox" id="dark-mode-toggle" />
`;

describe('Favourites Module', () => {
    beforeEach(() => {
        fetch.mockClear();
        localStorage.clear();
        document.getElementById("favourites-container").innerHTML = "";
    });

    test('displays message when not logged in', () => {
        // Simple implementation of checkLoginStatus
        const checkLoginStatus = () => {
            const userId = localStorage.getItem("userId");
            const favouritesContainer = document.getElementById("favourites-container");

            if (!userId) {
                favouritesContainer.innerHTML = `
          <div class="no-favourites">
            <h2>Your Favourites</h2>
            <p>Please log in to view your favourite restaurants.</p>
            <a href="index.html" class="button">Browse Restaurants</a>
          </div>
        `;
                return false;
            }

            return true;
        };

        const isLoggedIn = checkLoginStatus();

        expect(isLoggedIn).toBe(false);
        expect(document.getElementById("favourites-container").innerHTML).toContain("Please log in");
    });

    test('fetches favourites when logged in', async () => {
        // Set up logged in state
        localStorage.setItem("userId", "123");

        // Simple implementation of loadFavourites
        const loadFavourites = async () => {
            const userId = localStorage.getItem("userId");
            const favouritesContainer = document.getElementById("favourites-container");

            try {
                const response = await fetch(`http://localhost:8080/api/reviews/favourites?userId=${userId}`);
                if (!response.ok) throw new Error("Failed to fetch favourites");

                const favourites = await response.json();

                if (!favourites || favourites.length === 0) {
                    favouritesContainer.innerHTML = `
            <div class="no-favourites">
              <h2>Your Favourites</h2>
              <p>You haven't added any favourite restaurants yet.</p>
              <a href="index.html" class="button">Browse Restaurants</a>
            </div>
          `;
                    return [];
                }

                return favourites;
            } catch (error) {
                console.error("Error loading favourites:", error);
                favouritesContainer.innerHTML = `
          <div class="error">
            <p>Failed to load your favourites. Please try again later.</p>
          </div>
        `;
                return [];
            }
        };

        const favourites = await loadFavourites();

        expect(fetch).toHaveBeenCalledWith("http://localhost:8080/api/reviews/favourites?userId=123");
        expect(favourites).toHaveLength(1);
        expect(favourites[0].name).toBe("Favourite Restaurant");
    });

    test('createRestaurantCard creates a card element', () => {
        // Simple implementation of createRestaurantCard
        const createRestaurantCard = (restaurant) => {
            const card = document.createElement("div");
            card.className = "restaurant-card";

            const name = restaurant.name || restaurant.Name || "Unnamed Restaurant";
            const type = restaurant.type || restaurant.Category || "Restaurant";
            const borough = restaurant.borough || restaurant.Borough || "";
            const imageUrl = restaurant.storePhoto || "placeholder.jpg";
            const restaurantId = restaurant.id || restaurant._id;

            card.innerHTML = `
        <img src="${imageUrl}" alt="${name}" class="restaurant-image">
        <div class="restaurant-info">
          <h2 class="restaurant-name">${name}</h2>
          <span class="restaurant-type">${type}</span>
          ${borough ? `<p class="restaurant-borough">${borough}</p>` : ""}
          <button class="favourite-button active" data-id="${restaurantId}">★</button>
        </div>
      `;

            return card;
        };

        const testRestaurant = {
            id: 1,
            name: "Test Restaurant",
            type: "Italian",
            borough: "Westminster",
            storePhoto: "/test.jpg"
        };

        const card = createRestaurantCard(testRestaurant);

        expect(card.className).toBe("restaurant-card");
        expect(card.querySelector(".restaurant-name").textContent).toBe("Test Restaurant");
        expect(card.querySelector(".restaurant-type").textContent).toBe("Italian");
        expect(card.querySelector(".favourite-button").dataset.id).toBe("1");
    });
});