package org.example.Mappers;

import org.example.Models.Restaurant;
import org.example.DTOs.RestaurantDTO;

public class RestaurantMapper {
    public static RestaurantDTO toDTO(Restaurant restaurant) {
        return new RestaurantDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getType(),
                restaurant.getBorough(),
                restaurant.getStorePhoto(),
                restaurant.getPostcode()
        );
    }

    public static Restaurant toEntity(RestaurantDTO dto) {
        return new Restaurant(
                dto.getId(),
                dto.getName(),
                dto.getType(),
                dto.getBorough(),
                dto.getStorePhoto(),
                dto.getPostcode(),
                null
        );
    }
}