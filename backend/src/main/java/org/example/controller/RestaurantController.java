package org.example.controller;

import org.example.model.Restaurant;
import org.example.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/restaurants")
    public ResponseEntity<?> getAllRestaurants() {
        try {
            logger.info("Fetching all restaurants from database");

            List<Restaurant> restaurants = restaurantRepository.findAll();

            logger.info("Found {} restaurants", restaurants.size());

            return ResponseEntity.ok(restaurants);
        } catch (Exception e) {
            logger.error("Error retrieving restaurants", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/restaurants")
    public ResponseEntity<?> addRestaurant(@RequestBody Restaurant restaurant) {
        try {
            logger.info("Adding new restaurant: {}", restaurant.getName());

            // Set a default image if none provided
            if (restaurant.getStorePhoto() == null || restaurant.getStorePhoto().isEmpty()) {
                restaurant.setStorePhoto("/placeholder.svg?height=300&width=400&text=" + restaurant.getName());
            }

            Restaurant savedRestaurant = restaurantRepository.save(restaurant);

            logger.info("Successfully added restaurant with ID: {}", savedRestaurant.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedRestaurant);
        } catch (Exception e) {
            logger.error("Error adding restaurant", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

