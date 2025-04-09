package org.example;

import org.example.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, Integer> {
    boolean existsById(Integer id);
}