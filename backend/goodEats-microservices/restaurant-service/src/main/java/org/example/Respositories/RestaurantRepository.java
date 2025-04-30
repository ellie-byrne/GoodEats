// File: src/main/java/org/example/Respositories/RestaurantRepository.java
package org.example.Respositories;

import org.example.Models.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, Integer> {
    List<Restaurant> findByCuisine(String cuisine);
    List<Restaurant> findByPriceRange(String priceRange);
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}