package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
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
                logger.info("First restaurant: id={}, name={}, type={}, storePhoto={}",
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
}
