package org.example.Repositories;

import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewRepositoryTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    void findByRestaurantID_Success() {
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

        when(reviewRepository.findByRestaurantID(1)).thenReturn(reviews);

        List<Review> result = reviewRepository.findByRestaurantID(1);

        assertEquals(2, result.size());
        assertEquals("Great food!", result.get(0).getReview());
        assertEquals("Good service!", result.get(1).getReview());
    }

    @Test
    void findByRestaurantID_NoReviews() {
        when(reviewRepository.findByRestaurantID(999)).thenReturn(Arrays.asList());

        List<Review> result = reviewRepository.findByRestaurantID(999);

        assertEquals(0, result.size());
    }
}