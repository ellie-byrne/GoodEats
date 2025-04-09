// src/test/java/org/example/Controllers/RestaurantControllerTest.java
package org.example.Controllers;

import org.example.Models.Restaurant;
import org.example.Repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestaurantControllerTest {

    private RestaurantController restaurantController;
    private TestRestaurantRepository restaurantRepository;

    @BeforeEach
    public void setup() {
        restaurantRepository = new TestRestaurantRepository();
        restaurantController = new RestaurantController();

        // Set the repository using reflection
        try {
            java.lang.reflect.Field repoField = RestaurantController.class.getDeclaredField("restaurantRepository");
            repoField.setAccessible(true);
            repoField.set(restaurantController, restaurantRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testGetAllRestaurants_Success() {
        // Arrange
        List<Restaurant> restaurants = new ArrayList<>();
        Restaurant restaurant1 = new Restaurant(1, "Test Restaurant 1", "Italian", "Westminster", "photo1.jpg", null);
        Restaurant restaurant2 = new Restaurant(2, "Test Restaurant 2", "Chinese", "Camden", "photo2.jpg", null);

        restaurantRepository.save(restaurant1);
        restaurantRepository.save(restaurant2);

        // Act
        ResponseEntity<List<Restaurant>> response = restaurantController.getAllRestaurants();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size()); // Ensure we get both restaurants
    }

    @Test
    public void testGetAllRestaurants_EmptyList() {
        // Act
        ResponseEntity<List<Restaurant>> response = restaurantController.getAllRestaurants();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    public void testGetRestaurantById_Success() {
        // Arrange
        Restaurant restaurant = new Restaurant(1, "Test Restaurant", "Italian", "Westminster", "photo.jpg", null);
        restaurantRepository.save(restaurant);

        // Act
        ResponseEntity<Restaurant> response = restaurantController.getRestaurantById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Restaurant", response.getBody().getName());
    }

    @Test
    public void testGetRestaurantById_NotFound() {
        // Act
        ResponseEntity<Restaurant> response = restaurantController.getRestaurantById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Improved test repository implementation
    private static class TestRestaurantRepository implements RestaurantRepository {
        private final Map<Integer, Restaurant> restaurants = new HashMap<>();

        @Override
        public <S extends Restaurant> S save(S entity) {
            restaurants.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public Optional<Restaurant> findById(Integer id) {
            return Optional.ofNullable(restaurants.get(id));
        }

        @Override
        public boolean existsById(Integer integer) {
            return false;
        }

        @Override
        public <S extends Restaurant> List<S> saveAll(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public List<Restaurant> findAll() {
            return new ArrayList<>(restaurants.values());
        }

        @Override
        public Iterable<Restaurant> findAllById(Iterable<Integer> integers) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Integer integer) {

        }

        @Override
        public void delete(Restaurant entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Integer> integers) {

        }

        @Override
        public void deleteAll(Iterable<? extends Restaurant> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<Restaurant> findAll(Sort sort) {
            return List.of();
        }

        @Override
        public Page<Restaurant> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Restaurant> S insert(S entity) {
            return null;
        }

        @Override
        public <S extends Restaurant> List<S> insert(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public <S extends Restaurant> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Restaurant> List<S> findAll(Example<S> example) {
            return List.of();
        }

        @Override
        public <S extends Restaurant> List<S> findAll(Example<S> example, Sort sort) {
            return List.of();
        }

        @Override
        public <S extends Restaurant> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Restaurant> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Restaurant> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Restaurant, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        // Other methods remain the same
    }
}