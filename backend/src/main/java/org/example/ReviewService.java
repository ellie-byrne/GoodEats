package org.example;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Review createReview(String reviewBody, String restaurantId) {
        Review review = new Review(reviewBody);
        return reviewRepository.save(review);
    }
}