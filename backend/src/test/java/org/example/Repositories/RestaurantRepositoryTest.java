package org.example.Repositories;

import org.example.Models.Restaurant;
import org.example.Respositories.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantRepositoryTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Test
    void findById_Success() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);
        restaurant.setName("Test Restaurant");
        restaurant.setType("Italian");
        restaurant.setBorough("Redbridge");

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        Optional<Restaurant> result = restaurantRepository.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Test Restaurant", result.get().getName());
        assertEquals("Italian", result.get().getType());
    }

    @Test
    void findById_NotFound() {
        when(restaurantRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Restaurant> result = restaurantRepository.findById(999);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_Success() {
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1);
        restaurant1.setName("Test Restaurant 1");
        restaurant1.setType("Italian");
        restaurant1.setBorough("Redbridge");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2);
        restaurant2.setName("Test Restaurant 2");
        restaurant2.setType("Chinese");
        restaurant2.setBorough("Croydon");

        List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);

        when(restaurantRepository.findAll()).thenReturn(restaurants);

        List<Restaurant> result = restaurantRepository.findAll();

        assertEquals(2, result.size());
        assertEquals("Test Restaurant 1", result.get(0).getName());
        assertEquals("Test Restaurant 2", result.get(1).getName());
    }

    @Test
    void findAllById_Success() {
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1);
        restaurant1.setName("Test Restaurant 1");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2);
        restaurant2.setName("Test Restaurant 2");

        List<Restaurant> restaurants = Arrays.asList(restaurant1, restaurant2);
        List<Integer> ids = Arrays.asList(1, 2);

        when(restaurantRepository.findAllById(ids)).thenReturn(restaurants);

        List<Restaurant> result = restaurantRepository.findAllById(ids);

        assertEquals(2, result.size());
        assertEquals("Test Restaurant 1", result.get(0).getName());
        assertEquals("Test Restaurant 2", result.get(1).getName());
    }

    @Test
    void save_Success() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("New Restaurant");
        restaurant.setType("French");
        restaurant.setBorough("Hackney");

        Restaurant savedRestaurant = new Restaurant();
        savedRestaurant.setId(1);
        savedRestaurant.setName("New Restaurant");
        savedRestaurant.setType("French");
        savedRestaurant.setBorough("Hackney");

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(savedRestaurant);

        Restaurant result = restaurantRepository.save(restaurant);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("New Restaurant", result.getName());
    }
}