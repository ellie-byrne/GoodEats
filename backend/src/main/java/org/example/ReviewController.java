package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping()
    public ResponseEntity<Review> createReview(@RequestBody Map<String, Object> payload) {
        String userID = (String) payload.get("userID");
        String restaurantID = (String) payload.get("restaurantID");
        String reviewBody = (String) payload.get("reviewBody");
        int rating = (int) payload.get("rating");

        return new ResponseEntity<>(reviewService.createReview(userID, restaurantID, reviewBody, rating), HttpStatus.CREATED);
    }
}