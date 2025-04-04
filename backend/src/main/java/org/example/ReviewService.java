package org.example;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(String userID, String restaurantID, String reviewBody, int rating) {
        Review review = new Review(new ObjectId(), userID, restaurantID, new Date(), reviewBody, rating);
        return reviewRepository.save(review);
    }
}