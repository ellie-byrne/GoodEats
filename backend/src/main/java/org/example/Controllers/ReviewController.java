package org.example.Controllers;

import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.example.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/restaurants/{restaurantID}/reviews")
    public ResponseEntity<List<Review>> getReviewsForRestaurant(@PathVariable Integer restaurantID) {
        List<Review> reviews = reviewRepository.findByRestaurantID(restaurantID);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/reviews")
    public ResponseEntity<Review> createReview(@RequestBody Map<String, Object> payload) {
        Integer userID = (Integer) payload.get("userID");
        Integer restaurantID = (Integer) payload.get("restaurantID");
        String review = (String) payload.get("review");
        Integer rating = (Integer) payload.get("rating");

        return new ResponseEntity<>(reviewService.createReview(userID, restaurantID, review, rating), HttpStatus.CREATED);
    }
}