// File: src/main/java/org/example/Respositories/ReviewRepository.java
package org.example.Respositories;

import org.example.Models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, Integer> {
    List<Review> findByUserId(Integer userId);
    List<Review> findByRestaurantId(Integer restaurantId);
    List<Review> findByUserIdAndFavouriteIsTrue(Integer userId);
}