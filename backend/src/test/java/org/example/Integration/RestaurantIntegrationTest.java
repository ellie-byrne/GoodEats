package org.example.Integration;

import org.example.Models.Restaurant;
import org.example.Repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RestaurantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    public void setup() {
        restaurantRepository.deleteAll();

        Restaurant restaurant1 = new Restaurant(1, "Test Restaurant 1", "Italian", "Westminster", "photo1.jpg", new ArrayList<>());
        Restaurant restaurant2 = new Restaurant(2, "Test Restaurant 2", "Chinese", "Camden", "photo2.jpg", new ArrayList<>());

        restaurantRepository.save(restaurant1);
        restaurantRepository.save(restaurant2);
    }

    @Test
    public void testGetAllRestaurants() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("Test Restaurant 1"));
        assertTrue(content.contains("Test Restaurant 2"));
    }

    @Test
    public void testGetRestaurantById() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/restaurants/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("Test Restaurant 1"));
        assertTrue(content.contains("Italian"));
    }

    @Test
    public void testGetRestaurantById_NotFound() throws Exception {
        mockMvc.perform(get("/api/restaurants/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}