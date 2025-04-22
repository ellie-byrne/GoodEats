package org.example.Controllers;

import org.example.Models.Restaurant;
import org.example.Respositories.RestaurantRepository;
import org.example.DTOs.RestaurantDTO;
import org.example.Mappers.RestaurantMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantRepository.findAll();
            List<RestaurantDTO> dtos = restaurants.stream()
                    .map(RestaurantMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error retrieving restaurants: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Integer id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        return restaurant
                .map(value -> ResponseEntity.ok(RestaurantMapper.toDTO(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO dto) {
        try {
            Integer nextId = restaurantRepository.findAll().stream()
                    .map(Restaurant::getId)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;

            Restaurant newRestaurant = RestaurantMapper.toEntity(dto);
            newRestaurant.setId(nextId);

            Restaurant saved = restaurantRepository.save(newRestaurant);
            return ResponseEntity.status(201).body(RestaurantMapper.toDTO(saved));
        } catch (Exception e) {
            logger.error("Error creating restaurant: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) {
        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        restaurantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}