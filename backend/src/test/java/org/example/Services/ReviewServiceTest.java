// src/test/java/org/example/Services/ReviewServiceTest.java
package org.example.Services;

import org.example.Models.Review;
import org.example.TestUtils.TestMongoTemplate;
import org.example.TestUtils.TestReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewServiceTest {
    private ReviewService reviewService;
    private TestReviewRepository reviewRepository;
    private TestMongoTemplate mongoTemplate;

    @BeforeEach
    public void setup() {
        reviewRepository = new TestReviewRepository();
        mongoTemplate = new TestMongoTemplate();

        // Create a custom ReviewService that uses our test implementations
        reviewService = new ReviewService() {
            @Override
            public Review createReview(int userID, int restaurantID, String review, int rating) {
                return null;
            }

            @Override
            public Review updateReview(int id, String review, Integer rating) {
                return null;
            }
        };

        // We need to set the repository and mongoTemplate fields in ReviewService
        // This would normally be done by Spring's dependency injection
        // For testing without Mockito, we'll use reflection to set these fields
        try {
            java.lang.reflect.Field repoField = ReviewService.class.getDeclaredField("reviewRepository");
            repoField.setAccessible(true);
            repoField.set(reviewService, reviewRepository);

            java.lang.reflect.Field mongoField = ReviewService.class.getDeclaredField("mongoTemplate");
            mongoField.setAccessible(true);
            mongoField.set(reviewService, mongoTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testCreateReview_Success() {
        // Act
        Review result = reviewService.createReview(101, 201, "Great food and service!", 5);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(101, result.getUserID());
        assertEquals(201, result.getRestaurantID());
        assertEquals("Great food and service!", result.getReview());
        assertEquals(5, result.getRating());
        assertFalse(result.isFavourite());
        assertNotNull(result.getDate());
        assertEquals(1, reviewRepository.count());
    }

    @Test
    public void testUpdateReview_Success() {
        // Arrange
        Review review = new Review();
        review.setId(1);
        review.setUserID(101);
        review.setRestaurantID(201);
        review.setReview("Original review");
        review.setRating(3);
        review.setDate(new Date());
        reviewRepository.save(review);

        // Act
        // Use Integer.valueOf(1) to explicitly call the Integer version of the method
        Review result = reviewService.updateReview(Integer.valueOf(1), "Updated review text", 4);

        // Assert
        assertNotNull(result);
        assertEquals("Updated review text", result.getReview());
        assertEquals(4, result.getRating());
        assertEquals(1, reviewRepository.count());
    }

    @Test
    public void testUpdateReview_ReviewNotFound() {
        // Act
        // Use Integer.valueOf(999) to explicitly call the Integer version of the method
        Review result = reviewService.updateReview(Integer.valueOf(999), "Updated review text", 4);

        // Assert
        assertNull(result);
        assertEquals(0, reviewRepository.count());
    }

    @Test
    public void testUpdateReview_OnlyUpdateReviewText() {
        // Arrange
        Review review = new Review();
        review.setId(1);
        review.setUserID(101);
        review.setRestaurantID(201);
        review.setReview("Original review");
        review.setRating(3);
        review.setDate(new Date());
        reviewRepository.save(review);

        // Act
        // Use Integer.valueOf(1) to explicitly call the Integer version of the method
        Review result = reviewService.updateReview(Integer.valueOf(1), "Updated review text", null);

        // Assert
        assertNotNull(result);
        assertEquals("Updated review text", result.getReview());
        assertEquals(3, result.getRating()); // Rating should remain unchanged
    }

    @Test
    public void testUpdateReview_OnlyUpdateRating() {
        // Arrange
        Review review = new Review();
        review.setId(1);
        review.setUserID(101);
        review.setRestaurantID(201);
        review.setReview("Original review");
        review.setRating(3);
        review.setDate(new Date());
        reviewRepository.save(review);

        // Act
        // Use Integer.valueOf(1) to explicitly call the Integer version of the method
        Review result = reviewService.updateReview(Integer.valueOf(1), null, 4);

        // Assert
        assertNotNull(result);
        assertEquals("Original review", result.getReview()); // Review text should remain unchanged
        assertEquals(4, result.getRating());
    }

    @Test
    public void testUpdateReview_EmptyReviewText() {
        // Arrange
        Review review = new Review();
        review.setId(1);
        review.setUserID(101);
        review.setRestaurantID(201);
        review.setReview("Original review");
        review.setRating(3);
        review.setDate(new Date());
        reviewRepository.save(review);

        // Act
        // Use Integer.valueOf(1) to explicitly call the Integer version of the method
        Review result = reviewService.updateReview(Integer.valueOf(1), "", 4);

        // Assert
        assertNotNull(result);
        assertEquals("Original review", result.getReview()); // Review text should remain unchanged
        assertEquals(4, result.getRating());
    }
}