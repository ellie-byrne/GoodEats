// src/test/java/org/example/RestaurantControllerTest.java
package org.example.Controllers;

import org.example.Models.Restaurant;
import org.example.Respositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantControllerTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantController restaurantController;

    private Restaurant testRestaurant;
    private List<Restaurant> restaurantList;

    @BeforeEach
    void setUp() {
        testRestaurant = new Restaurant();
        testRestaurant.setId(1);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setType("Italian");
        testRestaurant.setBorough("Manhattan");
        testRestaurant.setStorePhoto("https://example.com/photo.jpg");

        restaurantList = new ArrayList<>();
        restaurantList.add(testRestaurant);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2);
        restaurant2.setName("Another Restaurant");
        restaurant2.setType("Chinese");
        restaurant2.setBorough("Brooklyn");
        restaurantList.add(restaurant2);
    }

    @Test
    void getAllRestaurants_Success() {
        // Arrange
        when(restaurantRepository.findAll()).thenReturn(restaurantList);

        // Act
        ResponseEntity<List<Restaurant>> response = restaurantController.getAllRestaurants();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Test Restaurant", response.getBody().get(0).getName());
        assertEquals("Another Restaurant", response.getBody().get(1).getName());
    }

    @Test
    void getAllRestaurants_EmptyList() {
        // Arrange
        when(restaurantRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Restaurant>> response = restaurantController.getAllRestaurants();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllRestaurants_Exception() {
        // Arrange
        when(restaurantRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<List<Restaurant>> response = restaurantController.getAllRestaurants();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getRestaurantById_Success() {
        // Arrange
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(testRestaurant));

        // Act
        ResponseEntity<Restaurant> response = restaurantController.getRestaurantById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Restaurant", response.getBody().getName());
    }

    @Test
    void getRestaurantById_NotFound() {
        // Arrange
        when(restaurantRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Restaurant> response = restaurantController.getRestaurantById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
