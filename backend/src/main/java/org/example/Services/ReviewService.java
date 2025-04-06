package org.example.Services;

import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Integer userID, Integer restaurantID, String review, Integer rating) {
        Review newReview = new Review(userID, restaurantID, new Date(), review, rating);
        return reviewRepository.save(newReview);
    }
}