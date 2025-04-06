// src/test/java/org/example/ReviewServiceTest.java
package org.example.Services;

import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;

    @BeforeEach
    void setUp() {
        testReview = new Review();
        testReview.setId(1);
        testReview.setUserID(1);
        testReview.setRestaurantID(1);
        testReview.setReview("Great food!");
        testReview.setRating(5);
        testReview.setDate(new Date());
    }

    @Test
    void createReview_Success() {
        // Arrange
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        Review result = reviewService.createReview(1, 1, "Great food!", 5);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUserID());
        assertEquals(1, result.getRestaurantID());
        assertEquals("Great food!", result.getReview());
        assertEquals(5, result.getRating());
        assertNotNull(result.getDate());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_ValidatesInput() {
        // Arrange
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act & Assert - Testing boundary conditions
        Review result = reviewService.createReview(1, 1, "Great food!", 5);
        assertEquals(5, result.getRating());

        // Test with invalid rating (should be clamped to 1-5)
        result = reviewService.createReview(1, 1, "Bad rating", 0);
        assertEquals(1, result.getRating());

        result = reviewService.createReview(1, 1, "Bad rating", 6);
        assertEquals(5, result.getRating());
    }
}