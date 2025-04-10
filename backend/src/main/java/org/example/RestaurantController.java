package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/restaurants")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        try {
            logger.info("Attempting to fetch restaurants from 'GoodEats' collection");
            List<Restaurant> restaurants = restaurantRepository.findAll();
            logger.info("Query complete. Found {} restaurants", restaurants.size());
            if (!restaurants.isEmpty()) {
                Restaurant first = restaurants.get(0);
                logger.info("First restaurant: id={}, name={}, type={}, borough={}",
                        first.getId() != null ? first.getId() : "null",
                        first.getName() != null ? first.getName() : "null",
                        first.getType() != null ? first.getType() : "null",
                        first.getBorough() != null ? first.getBorough() : "null");
            }
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving restaurants: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Integer id) {

        return restaurantRepository.findById(id)
                .map(restaurant -> new ResponseEntity<>(restaurant, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/restaurants")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        try {
            // Generate a new ID (highest existing ID + 1)
            Integer nextId = 1;
            List<Restaurant> allRestaurants = restaurantRepository.findAll();
            if (!allRestaurants.isEmpty()) {
                nextId = allRestaurants.stream()
                        .map(r -> (Integer) r.getId())
                        .max(Integer::compare)
                        .orElse(0) + 1;
            }
            restaurant.setId(nextId);

            // Save the new restaurant
            Restaurant savedRestaurant = restaurantRepository.save(restaurant);
            return new ResponseEntity<>(savedRestaurant, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating restaurant: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) {
        try {
            if (!restaurantRepository.existsById(id)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            restaurantRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting restaurant: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}