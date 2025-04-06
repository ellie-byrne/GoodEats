// src/test/java/org/example/ReviewControllerTest.java
package org.example.Controllers;

import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.example.Services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewController reviewController;

    private Review testReview;
    private List<Review> reviewList;
    private Map<String, Object> reviewPayload;

    @BeforeEach
    void setUp() {
        testReview = new Review();
        testReview.setId(1);
        testReview.setUserID(1);
        testReview.setRestaurantID(1);
        testReview.setReview("Great food!");
        testReview.setRating(5);
        testReview.setDate(new Date());

        reviewList = new ArrayList<>();
        reviewList.add(testReview);

        Review review2 = new Review();
        review2.setId(2);
        review2.setUserID(2);
        review2.setRestaurantID(1);
        review2.setReview("Good service!");
        review2.setRating(4);
        review2.setDate(new Date());
        reviewList.add(review2);

        reviewPayload = new HashMap<>();
        reviewPayload.put("userID", 1);
        reviewPayload.put("restaurantID", 1);
        reviewPayload.put("review", "Great food!");
        reviewPayload.put("rating", 5);
    }

    @Test
    void getReviewsForRestaurant_Success() {
        // Arrange
        when(reviewRepository.findByRestaurantID(1)).thenReturn(reviewList);

        // Act
        ResponseEntity<List<Review>> response = reviewController.getReviewsForRestaurant(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Great food!", response.getBody().get(0).getReview());
        assertEquals("Good service!", response.getBody().get(1).getReview());
    }

    @Test
    void getReviewsForRestaurant_EmptyList() {
        // Arrange
        when(reviewRepository.findByRestaurantID(999)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Review>> response = reviewController.getReviewsForRestaurant(999);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void createReview_Success() {
        // Arrange
        when(reviewService.createReview(anyInt(), anyInt(), anyString(), anyInt())).thenReturn(testReview);

        // Act
        ResponseEntity<Review> response = reviewController.createReview(reviewPayload);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Great food!", response.getBody().getReview());
        assertEquals(5, response.getBody().getRating());
    }

    @Test
    void createReview_HandlesInvalidPayload() {
        // Arrange - Missing fields
        Map<String, Object> invalidPayload = new HashMap<>();
        invalidPayload.put("userID", 1);
        // Missing restaurantID, review, rating

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            reviewController.createReview(invalidPayload);
        });
    }
}