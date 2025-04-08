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
            Integer rating = payload.get("rating") != null ? (Integer) payload.get("rating") : null;
            Boolean favourite = payload.get("favourite") != null ? (Boolean) payload.get("favourite") : null;

            Review existing = reviewRepository.findById(id).orElse(null);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }

            // Only update fields if values are provided
            if (reviewText != null && !reviewText.trim().isEmpty()) {
                existing.setReview(reviewText);
            }

            if (rating != null) {
                existing.setRating(rating);
            }

            if (favourite != null) {
                existing.setFavourite(favourite);
            }

            Review updated = reviewRepository.save(existing);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            System.out.println("Update error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/reviews/{id}/favourite")
    public ResponseEntity<Review> updateFavourite(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            Boolean favourite = (Boolean) payload.get("favourite");
            Review review = reviewRepository.findById(id).orElse(null);
            if (review == null) return ResponseEntity.notFound().build();

            review.setFavourite(favourite);
            return ResponseEntity.ok(reviewRepository.save(review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        reviewRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}