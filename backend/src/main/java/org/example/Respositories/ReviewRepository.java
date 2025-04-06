package org.example.Respositories;

import org.example.Models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByRestaurantID(Integer restaurantID);
}