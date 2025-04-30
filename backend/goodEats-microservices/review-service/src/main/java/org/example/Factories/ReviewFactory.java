// File: src/main/java/org/example/Factories/ReviewFactory.java
package org.example.Factories;

import org.example.Models.Review;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ReviewFactory {
    public Review createReview(Integer userId, Integer restaurantId, String reviewText, int rating, boolean favourite) {
        Review review = new Review();
        review.setUserId(userId);
        review.setRestaurantId(restaurantId);
        review.setDate(new Date());
        review.setReview(reviewText);
        review.setRating(rating);
        review.setFavourite(favourite);
        return review;
    }
}