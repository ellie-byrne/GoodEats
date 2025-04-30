// File: src/main/java/org/example/Mappers/RestaurantMapper.java
package org.example.Mappers;

import org.example.DTOs.RestaurantDTO;
import org.example.Models.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {
    public RestaurantDTO toDTO(Restaurant restaurant) {
        return new RestaurantDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCuisine(),
                restaurant.getLocation(),
                restaurant.getPriceRange(),
                restaurant.getImageUrl(),
                restaurant.getAverageRating()
        );
    }
}