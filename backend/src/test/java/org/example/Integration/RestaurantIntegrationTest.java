// src/test/java/org/example/RestaurantIntegrationTest.java
package org.example.Integration;

import org.example.Models.Restaurant;
import org.example.Respositories.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantRepository restaurantRepository;

    @Test
    void getAllRestaurants() throws Exception {
        // Arrange
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setId(1);
        restaurant1.setName("Test Restaurant 1");
        restaurant1.setType("Italian");
        restaurant1.setBorough("Manhattan");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setId(2);
        restaurant2.setName("Test Restaurant 2");
        restaurant2.setType("Chinese");
        restaurant2.setBorough("Brooklyn");

        when(restaurantRepository.findAll()).thenReturn(Arrays.asList(restaurant1, restaurant2));

        // Act & Assert
        mockMvc.perform(get("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Restaurant 1")))
                .andExpect(jsonPath("$[0].type", is("Italian")))
                .andExpect(jsonPath("$[1].name", is("Test Restaurant 2")))
                .andExpect(jsonPath("$[1].type", is("Chinese")));
    }

    @Test
    void getRestaurantById() throws Exception {
        // Arrange
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1);
        restaurant.setName("Test Restaurant");
        restaurant.setType("Italian");
        restaurant.setBorough("Manhattan");

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert - Success case
        mockMvc.perform(get("/api/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Restaurant")))
                .andExpect(jsonPath("$.type", is("Italian")));

        // Act & Assert - Not found case
        mockMvc.perform(get("/api/restaurants/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
