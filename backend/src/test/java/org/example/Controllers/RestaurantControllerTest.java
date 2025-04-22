package org.example.Controllers;

import org.example.Models.Restaurant;
import org.example.DTOs.RestaurantDTO;
import org.example.Mappers.RestaurantMapper;
import org.example.Respositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantControllerTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantController restaurantController;

    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        testRestaurant = new Restaurant();
        testRestaurant.setId(1);
        testRestaurant.setName("Testaurant");
        testRestaurant.setType("Italian");
        testRestaurant.setBorough("Camden");
        testRestaurant.setStorePhoto("https://example.com/photo.jpg");
    }

    @Test
    void getAllRestaurants_Success() {
        List<Restaurant> restaurants = List.of(
                testRestaurant,
                new Restaurant(2, "Yummy Palace", "Chinese", "Hackney", null, null)
        );
        when(restaurantRepository.findAll()).thenReturn(restaurants);

        ResponseEntity<List<RestaurantDTO>> response = restaurantController.getAllRestaurants();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Testaurant", response.getBody().get(0).getName());
    }

    @Test
    void getAllRestaurants_EmptyList() {
        when(restaurantRepository.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<List<RestaurantDTO>> response = restaurantController.getAllRestaurants();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllRestaurants_Exception() {
        when(restaurantRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<List<RestaurantDTO>> response = restaurantController.getAllRestaurants();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getRestaurantById_Found() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(testRestaurant));

        ResponseEntity<RestaurantDTO> response = restaurantController.getRestaurantById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Testaurant", response.getBody().getName());
    }

    @Test
    void getRestaurantById_NotFound() {
        when(restaurantRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<RestaurantDTO> response = restaurantController.getRestaurantById(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}