// src/test/java/org/example/RestaurantRepositoryTest.java
package org.example.Repositories;

import org.example.Models.Restaurant;
import org.example.Respositories.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataMongoTest
public class RestaurantRepositoryTest {

    @MockBean
    private MongoTemplate mongoTemplate;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void findById_Success() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);
        restaurant.setName("Test Restaurant");

        when(mongoTemplate.findById(1, Restaurant.class)).thenReturn(restaurant);

        // Act
        Optional<Restaurant> result = restaurantRepository.findById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Restaurant", result.get().getName());
    }

    @Test
    void findById_NotFound() {
        // Arrange
        when(mongoTemplate.findById(999, Restaurant.class)).thenReturn(null);

        // Act
        Optional<Restaurant> result = restaurantRepository.findById(999);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_Success() {
        // Arrange
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1);
        restaurant1.setName("Test Restaurant 1");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2);
        restaurant2.setName("Test Restaurant 2");

        List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);

        when(mongoTemplate.findAll(Restaurant.class)).thenReturn(restaurants);

        // Act
        List<Restaurant> result = restaurantRepository.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Test Restaurant 1", result.get(0).getName());
        assertEquals("Test Restaurant 2", result.get(1).getName());
    }
}