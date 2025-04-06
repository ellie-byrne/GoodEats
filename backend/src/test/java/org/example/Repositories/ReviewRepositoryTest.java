// src/test/java/org/example/ReviewRepositoryTest.java
package org.example.Repositories;

import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DataMongoTest
public class ReviewRepositoryTest {

    @MockBean
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void findByRestaurantID_Success() {
        // Arrange
        Review review1 = new Review();
        review1.setId(1);
        review1.setUserID(1);
        review1.setRestaurantID(1);
        review1.setReview("Great food!");
        review1.setRating(5);
        review1.setDate(new Date());

        Review review2 = new Review();
        review2.setId(2);
        review2.setUserID(2);
        review2.setRestaurantID(1);
        review2.setReview("Good service!");
        review2.setRating(4);
        review2.setDate(new Date());

        List<Review> reviews = Arrays.asList(review1, review2);

        when(mongoTemplate.find(any(Query.class), eq(Review.class))).thenReturn(reviews);

        // Act
        List<Review> result = reviewRepository.findByRestaurantID(1);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Great food!", result.get(0).getReview());
        assertEquals("Good service!", result.get(1).getReview());
    }

    @Test
    void findByRestaurantID_NoReviews() {
        // Arrange
        when(mongoTemplate.find(any(Query.class), eq(Review.class))).thenReturn(Arrays.asList());

        // Act
        List<Review> result = reviewRepository.findByRestaurantID(999);

        // Assert
        assertEquals(0, result.size());
    }
}