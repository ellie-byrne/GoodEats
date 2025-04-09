// src/test/java/org/example/Models/ReviewTest.java
package org.example.Models;

import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewTest {

    @Test
    public void testReviewGettersAndSetters() {
        // Arrange
        Review review = new Review();
        Date now = new Date();

        // Act
        review.setId(1);
        review.setUserID(101);
        review.setRestaurantID(201);
        review.setReview("Great food and service!");
        review.setRating(5);
        review.setDate(now);
        review.setFavourite(true);

        // Assert
        assertEquals(Integer.valueOf(1), review.getId()); // Changed to Integer.valueOf(1)
        assertEquals(Integer.valueOf(101), review.getUserID()); // Changed to Integer.valueOf(101)
        assertEquals(Integer.valueOf(201), review.getRestaurantID()); // Changed to Integer.valueOf(201)
        assertEquals("Great food and service!", review.getReview());
        assertEquals(Integer.valueOf(5), review.getRating()); // Changed to Integer.valueOf(5)
        assertEquals(now, review.getDate());
        assertTrue(review.isFavourite());
    }

    @Test
    public void testDefaultValues() {
        // Arrange & Act
        Review review = new Review();

        // Assert
        assertNull(review.getId()); // Changed to assertNull
        assertNull(review.getUserID()); // Changed to assertNull
        assertNull(review.getRestaurantID()); // Changed to assertNull
        assertNull(review.getReview());
        assertNull(review.getRating()); // Changed to assertNull
        assertNull(review.getDate());
        assertFalse(review.isFavourite());
    }
}