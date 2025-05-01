package org.example.Controllers;

import org.example.Models.Review;
import org.example.Models.Restaurant;
import org.example.Respositories.ReviewRepository;
import org.example.Respositories.RestaurantRepository;
import org.example.Services.ReviewService;
import org.example.DTOs.ReviewDTO;
import org.example.DTOs.CreateReviewRequest;
import org.example.DTOs.UpdateReviewRequest;
import org.example.Mappers.ReviewMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReviewController {

    public final ReviewService reviewService;
    public final ReviewRepository reviewRepository;
    public final RestaurantRepository restaurantRepository;

    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository, RestaurantRepository restaurantRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/restaurants/{restaurantID}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsForRestaurant(@PathVariable Integer restaurantID) {
        List<Review> reviews = reviewRepository.findByRestaurantID(restaurantID);

        List<ReviewDTO> dtos = reviews.stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody CreateReviewRequest request) {
        try {
            Review newReview = reviewService.createReview(
                    request.getUserID(),
                    request.getRestaurantID(),
                    request.getReview(),
                    request.getRating()
            );
            newReview.setFavourite(request.isFavourite());

            Review saved = reviewRepository.save(newReview);
            return new ResponseEntity<>(ReviewMapper.toDTO(saved), HttpStatus.CREATED);

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Integer id, @RequestBody UpdateReviewRequest request) {
        try {
            Review existing = reviewRepository.findById(id).orElse(null);
            if (existing == null) return ResponseEntity.notFound().build();

            if (request.getReview() != null && !request.getReview().trim().isEmpty())
                existing.setReview(request.getReview());

            if (request.getRating() != null)
                existing.setRating(request.getRating());

            if (request.getFavourite() != null)
                existing.setFavourite(request.getFavourite());

            Review updated = reviewRepository.save(existing);
            return ResponseEntity.ok(ReviewMapper.toDTO(updated));

        } catch (Exception e) {
            System.out.println("Update error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reviews/favourites")
    public ResponseEntity<List<Restaurant>> getUserFavouriteRestaurants(@RequestParam Integer userId) {
        List<Review> favReviews = reviewRepository.findByUserIDAndFavourite(userId, true);
        List<Integer> restaurantIds = favReviews.stream()
                .map(Review::getRestaurantID)
                .collect(Collectors.toList());
        List<Restaurant> favourites = restaurantRepository.findAllById(restaurantIds);
        return ResponseEntity.ok(favourites);
    }

    @PutMapping("/reviews/{id}/favourite")
    public ResponseEntity<ReviewDTO> updateFavourite(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            Boolean favourite = (Boolean) payload.get("favourite");
            Review review = reviewRepository.findById(id).orElse(null);
            if (review == null) return ResponseEntity.notFound().build();

            review.setFavourite(favourite);
            Review updated = reviewRepository.save(review);
            return ResponseEntity.ok(ReviewMapper.toDTO(updated));

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