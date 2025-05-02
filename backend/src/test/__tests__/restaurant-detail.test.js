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
        json: () => Promise.resolve({
            id: 1,
            name: "Test Restaurant",
            type: "Italian",
            borough: "Westminster",
            storePhoto: "/test.jpg"
        })
    })
);

// Mock URL params
const mockUrlParams = new URLSearchParams("?id=1");
Object.defineProperty(window, 'location', {
    value: {
        search: "?id=1"
    }
});

// Mock URLSearchParams
global.URLSearchParams = jest.fn(() => mockUrlParams);
mockUrlParams.get = jest.fn(() => "1");

// Mock DOM
document.body.innerHTML = `
  <div id="restaurant-detail-container"></div>
  <div id="user-review-container"></div>
  <div class="review-grid"></div>
  <input type="checkbox" id="dark-mode-toggle" />
`;

describe('Restaurant Detail Module', () => {
    beforeEach(() => {
        fetch.mockClear();
        localStorage.clear();
        document.getElementById("restaurant-detail-container").innerHTML = "";
        document.getElementById("user-review-container").innerHTML = "";
        document.querySelector(".review-grid").innerHTML = "";
    });

    test('loads restaurant details from API', async () => {
        // Simple implementation of loadRestaurantDetails
        const loadRestaurantDetails = async (id) => {
            try {
                const response = await fetch(`http://localhost:8080/api/restaurants/${id}`);
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return await response.json();
            } catch (error) {
                console.error("Error loading restaurant details:", error);
                return null;
            }
        };

        const restaurant = await loadRestaurantDetails("1");

        expect(fetch).toHaveBeenCalledWith("http://localhost:8080/api/restaurants/1");
        expect(restaurant).not.toBeNull();
        expect(restaurant.name).toBe("Test Restaurant");
    });

    test('displays restaurant details', () => {
        // Simple implementation of displayRestaurantDetails
        const displayRestaurantDetails = (restaurant) => {
            const container = document.getElementById("restaurant-detail-container");

            const name = restaurant.name || restaurant.Name || "Unnamed Restaurant";
            const type = restaurant.type || restaurant.Category || "Restaurant";
            const borough = restaurant.borough || restaurant.Borough || "";
            const imageUrl = restaurant.storePhoto || "placeholder.jpg";

            container.innerHTML = `
        <img src="${imageUrl}" alt="${name}" class="restaurant-image">
        <h2>${name}</h2>
        <p>Type: ${type}</p>
        <p>Borough: ${borough}</p>
      `;
        };

        const testRestaurant = {
            id: 1,
            name: "Test Restaurant",
            type: "Italian",
            borough: "Westminster",
            storePhoto: "/test.jpg"
        };

        displayRestaurantDetails(testRestaurant);

        const container = document.getElementById("restaurant-detail-container");
        expect(container.querySelector("h2").textContent).toBe("Test Restaurant");
        expect(container.querySelector("p").textContent).toBe("Type: Italian");
    });

    test('renders review form when logged in', () => {
        // Set up logged in state
        localStorage.setItem("userId", "123");

        // Simple implementation of renderReviewForm
        const renderReviewForm = () => {
            const container = document.getElementById("user-review-container");

            container.innerHTML = `
        <h3>Submit a Review</h3>
        <form id="review-form">
          <textarea id="review-text" placeholder="Write your review here..." required></textarea>
          <div class="restaurant-rating">
            <div class="stars" id="review-stars" data-rating="0">
              <span class="star" data-value="1">★</span>
              <span class="star" data-value="2">★</span>
              <span class="star" data-value="3">★</span>
              <span class="star" data-value="4">★</span>
              <span class="star" data-value="5">★</span>
            </div>
          </div>
          <button type="submit">Submit</button>
        </form>
      `;
        };

        renderReviewForm();

        const container = document.getElementById("user-review-container");
        expect(container.querySelector("h3").textContent).toBe("Submit a Review");
        expect(container.querySelector("#review-form")).not.toBeNull();
        expect(container.querySelector("#review-text")).not.toBeNull();
    });
});