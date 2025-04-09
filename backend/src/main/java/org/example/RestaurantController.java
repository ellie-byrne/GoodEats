package org.example;

import org.example.Restaurant;
import org.example.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
                logger.info("First restaurant: id={}, name={}, type={}, borough={}, storePhoto={}",
                        first.getId() != null ? first.getId() : "null",
                        first.getName() != null ? first.getName() : "null",
                        first.getType() != null ? first.getType() : "null",
                        first.getStorePhoto() != null ? first.getStorePhoto() : "null");
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
            logger.info("Creating new restaurant: {}", restaurant.getName());

            // Find the highest existing ID and increment it by 1
            Integer newId = 1;
            List<Restaurant> restaurants = restaurantRepository.findAll();
            if (!restaurants.isEmpty()) {
                for (Restaurant r : restaurants) {
                    if (r.getId() != null) {
                        Integer id = (Integer) r.getId();
                        if (id >= newId) {
                            newId = id + 1;
                        }
                    }
                }
            }

            restaurant.setId(newId);
            Restaurant savedRestaurant = restaurantRepository.save(restaurant);
            logger.info("Restaurant created with ID: {}", savedRestaurant.getId());

            return new ResponseEntity<>(savedRestaurant, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating restaurant: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<HttpStatus> deleteRestaurant(@PathVariable Integer id) {
        try {
            logger.info("Deleting restaurant with ID: {}", id);

            if (!restaurantRepository.existsById(id)) {
                logger.warn("Restaurant with ID {} not found", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            restaurantRepository.deleteById(id);
            logger.info("Restaurant with ID {} deleted successfully", id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting restaurant: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}