document.addEventListener("DOMContentLoaded", function() {
    //get restaurants from rest API
    fetch("/api/restaurants")
        .then(response => {
            if (!response.ok) {
                throw new Error("response was not ok");
            }
            return response.json();
        })
        .then(restaurants => {
            console.log("Restaurants loaded:", restaurants);
            //need to display restaurants
            document.getElementById("restaurants-container").textContent =
                "Loaded ${restaurants.length} restaurants";
        })
        .catch(error => {
            console.error("Error fetching restaurants:", error);
            document.getElementById("restaurants-container").textContent =
                "Error loading restaurants.';
        });
});