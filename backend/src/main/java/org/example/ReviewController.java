package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        try {
            Integer userID = (Integer) payload.get("userID");
            Integer restaurantID = Integer.parseInt(payload.get("restaurantID").toString());
            String review = (String) payload.get("review");
            Integer rating = (Integer) payload.get("rating");
            Boolean favourite = payload.get("favourite") != null && (Boolean) payload.get("favourite");

            Review newReview = reviewService.createReview(userID, restaurantID, review, rating);
            newReview.setFavourite(favourite);
            return new ResponseEntity<>(reviewRepository.save(newReview), HttpStatus.CREATED);

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // Update an existing review
    @PutMapping("/reviews/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            String reviewText = (String) payload.get("review");
            Integer rating = (Integer) payload.get("rating");
            Boolean favourite = payload.get("favourite") != null && (Boolean) payload.get("favourite");

            Review updatedReview = reviewService.updateReview(id, reviewText, rating);
            if (updatedReview == null) {
                return ResponseEntity.notFound().build();
            }

            if (favourite != null) {
                updatedReview.setFavourite(favourite);
                reviewRepository.save(updatedReview);
            }

            return ResponseEntity.ok(updatedReview);

        } catch (Exception e) {
            System.out.println("Update error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}