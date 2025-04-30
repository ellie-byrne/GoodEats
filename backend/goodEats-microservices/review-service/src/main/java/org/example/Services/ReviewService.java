// File: src/main/java/org/example/Services/ReviewService.java
package org.example.Services;

import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public static class Counter {
        private int value;

        public Counter() {
        }

        public Counter(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public List<Review> getReviewsByRestaurantId(Integer restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }

    public List<Review> getReviewsByUserId(Integer userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getFavoritesByUserId(Integer userId) {
        return reviewRepository.findByUserIdAndFavouriteIsTrue(userId);
    }

    public Review updateReview(Integer id, String reviewText, Integer rating, Boolean favourite) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);
        if (reviewOptional.isPresent()) {
            Review existing = reviewOptional.get();

            if (reviewText != null) {
                existing.setReview(reviewText);
            }

            if (rating != null) {
                existing.setRating(rating);
            }

            if (favourite != null) {
                existing.setFavourite(favourite);
            }

            return reviewRepository.save(existing);
        }
        return null;
    }
}