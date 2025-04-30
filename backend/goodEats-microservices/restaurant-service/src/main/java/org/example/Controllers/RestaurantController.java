// File: src/main/java/org/example/Controllers/RestaurantController.java
package org.example.Controllers;

import org.example.DTOs.RestaurantDTO;
import org.example.Mappers.RestaurantMapper;
import org.example.Models.Restaurant;
import org.example.Respositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public RestaurantController(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<RestaurantDTO> restaurantDTOs = restaurants.stream()
                .map(restaurantMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(restaurantDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Integer id) {
        return restaurantRepository.findById(id)
                .map(restaurantMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(@RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) String cuisine,
                                                                 @RequestParam(required = false) String priceRange) {
        List<Restaurant> restaurants;

        if (name != null && !name.isEmpty()) {
            restaurants = restaurantRepository.findByNameContainingIgnoreCase(name);
        } else if (cuisine != null && !cuisine.isEmpty()) {
            restaurants = restaurantRepository.findByCuisine(cuisine);
        } else if (priceRange != null && !priceRange.isEmpty()) {
            restaurants = restaurantRepository.findByPriceRange(priceRange);
        } else {
            restaurants = restaurantRepository.findAll();
        }

        List<RestaurantDTO> restaurantDTOs = restaurants.stream()
                .map(restaurantMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(restaurantDTOs);
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getRestaurantReviews(@PathVariable Integer id) {
        // In a microservices architecture, we would call the review service API
        // to get reviews for this restaurant
        return ResponseEntity.ok().body("This endpoint would call the review service API to get restaurant reviews");
    }

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDTO.getName());
        restaurant.setCuisine(restaurantDTO.getCuisine());
        restaurant.setLocation(restaurantDTO.getLocation());
        restaurant.setPriceRange(restaurantDTO.getPriceRange());
        restaurant.setImageUrl(restaurantDTO.getImageUrl());
        restaurant.setAverageRating(restaurantDTO.getAverageRating());

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return ResponseEntity.ok(restaurantMapper.toDTO(savedRestaurant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@PathVariable Integer id, @RequestBody RestaurantDTO restaurantDTO) {
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    restaurant.setName(restaurantDTO.getName());
                    restaurant.setCuisine(restaurantDTO.getCuisine());
                    restaurant.setLocation(restaurantDTO.getLocation());
                    restaurant.setPriceRange(restaurantDTO.getPriceRange());
                    restaurant.setImageUrl(restaurantDTO.getImageUrl());
                    restaurant.setAverageRating(restaurantDTO.getAverageRating());
                    Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
                    return ResponseEntity.ok(restaurantMapper.toDTO(updatedRestaurant));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) {
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    restaurantRepository.delete(restaurant);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}