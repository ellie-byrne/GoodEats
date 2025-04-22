package org.example.Respositories;

import org.example.Models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, Integer> {
    List<Review> findByRestaurantID(Integer restaurantID);
    List<Review> findByUserIDAndFavourite(int userID, boolean favourite);
}