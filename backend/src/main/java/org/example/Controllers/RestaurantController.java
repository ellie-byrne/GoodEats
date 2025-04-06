package org.example.Controllers;

import org.example.Models.Restaurant;
import org.example.Respositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;

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
}