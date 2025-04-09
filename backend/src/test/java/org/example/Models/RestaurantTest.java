// src/test/java/org/example/Models/RestaurantTest.java
package org.example.Models;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    @Test
    public void testRestaurantConstructorAndGetters() {
        // Arrange
        List<String> menu = new ArrayList<>();
        menu.add("Pizza");
        menu.add("Pasta");

        // Act
        Restaurant restaurant = new Restaurant(1, "Test Restaurant", "Italian", "Westminster", "photo.jpg", menu);

        // Assert
        assertEquals(Integer.valueOf(1), restaurant.getId()); // Changed to Integer.valueOf(1)
        assertEquals("Test Restaurant", restaurant.getName());
        assertEquals("Italian", restaurant.getType());
        assertEquals("Westminster", restaurant.getLocation());
        assertEquals("photo.jpg", restaurant.getPhoto());
        assertEquals(2, restaurant.getMenu().size());
        assertTrue(restaurant.getMenu().contains("Pizza"));
    }

    @Test
    public void testRestaurantSetters() {
        // Arrange
        Restaurant restaurant = new Restaurant();
        List<String> menu = new ArrayList<>();
        menu.add("Burger");

        // Act
        restaurant.setId(2);
        restaurant.setName("Burger Place");
        restaurant.setType("Fast Food");
        restaurant.setLocation("Camden");
        restaurant.setPhoto("burger.jpg");
        restaurant.setMenu(menu);

        // Assert
        assertEquals(Integer.valueOf(2), restaurant.getId()); // Changed to Integer.valueOf(2)
        assertEquals("Burger Place", restaurant.getName());
        assertEquals("Fast Food", restaurant.getType());
        assertEquals("Camden", restaurant.getLocation());
        assertEquals("burger.jpg", restaurant.getPhoto());
        assertEquals(1, restaurant.getMenu().size());
        assertTrue(restaurant.getMenu().contains("Burger"));
    }
}