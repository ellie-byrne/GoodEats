// File: src/main/java/org/example/Controllers/ReviewController.java
package org.example.Controllers;

import org.example.DTOs.CreateReviewRequest;
import org.example.DTOs.ReviewDTO;
import org.example.DTOs.UpdateReviewRequest;
import org.example.Factories.ReviewFactory;
import org.example.Mappers.ReviewMapper;
import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.example.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;
    private final ReviewFactory reviewFactory;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public ReviewController(ReviewRepository reviewRepository, ReviewService reviewService, ReviewMapper reviewMapper, ReviewFactory reviewFactory) {
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
        this.reviewFactory = reviewFactory;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Integer id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUserId(@PathVariable Integer userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByRestaurantId(@PathVariable Integer restaurantId) {
        List<Review> reviews = reviewService.getReviewsByRestaurantId(restaurantId);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    @GetMapping("/user/{userId}/favorites")
    public ResponseEntity<List<ReviewDTO>> getFavoritesByUserId(@PathVariable Integer userId) {
        List<Review> favorites = reviewService.getFavoritesByUserId(userId);
        List<ReviewDTO> favoriteDTOs = favorites.stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favoriteDTOs);
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody CreateReviewRequest request) {
        Review review = reviewFactory.createReview(
                request.getUserId(),
                request.getRestaurantId(),
                request.getReview(),
                request.getRating(),
                request.isFavourite()
        );
        Review savedReview = reviewRepository.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toDTO(savedReview));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Integer id, @RequestBody UpdateReviewRequest request) {
        Review updatedReview = reviewService.updateReview(
                id,
                request.getReview(),
                request.getRating(),
                request.getFavourite()
        );

        if (updatedReview != null) {
            return ResponseEntity.ok(reviewMapper.toDTO(updatedReview));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<ReviewDTO> toggleFavorite(@PathVariable Integer id) {
        return reviewRepository.findById(id)
                .map(review -> {
                    review.setFavourite(!review.isFavourite());
                    Review savedReview = reviewRepository.save(review);
                    return ResponseEntity.ok(reviewMapper.toDTO(savedReview));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        return reviewRepository.findById(id)
                .map(review -> {
                    reviewRepository.delete(review);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}