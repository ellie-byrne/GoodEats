package org.example.service;

import org.example.model.Restaurant;
import org.example.model.Review;
import org.example.repository.RestaurantRepository;
import org.example.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Review createReview(String reviewBody, Long restaurantId) {
        Review review = new Review(reviewBody);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        review.setRestaurant(restaurant);
        return reviewRepository.save(review);
    }
}

