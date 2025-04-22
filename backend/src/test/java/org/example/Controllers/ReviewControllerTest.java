package org.example.Controllers;

import org.example.DTOs.CreateReviewRequest;
import org.example.DTOs.ReviewDTO;
import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.example.Services.ReviewService;
import org.example.Mappers.ReviewMapper;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private Review testReview;

    @BeforeEach
    void setUp() {
        testReview = new Review();
        testReview.setId(1);
        testReview.setUserID(1);
        testReview.setRestaurantID(100);
        testReview.setReview("Amazing experience!");
        testReview.setRating(5);
        testReview.setFavourite(true);
        testReview.setDate(new Date());
    }

    @Test
    void getReviewsForRestaurant_Success() {
        List<Review> reviews = Collections.singletonList(testReview);
        when(reviewRepository.findByRestaurantID(100)).thenReturn(reviews);

        ResponseEntity<List<ReviewDTO>> response = reviewController.getReviewsForRestaurant(100);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Amazing experience!", response.getBody().get(0).getReview());
    }

    @Test
    void createReview_Success() {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setUserID(1);
        request.setRestaurantID(100);
        request.setReview("Fantastic food!");
        request.setRating(4);
        request.setFavourite(true);

        when(reviewService.createReview(any(), any(), any(), any())).thenReturn(testReview);
        when(reviewRepository.save(any())).thenReturn(testReview);

        ResponseEntity<ReviewDTO> response = reviewController.createReview(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Amazing experience!", response.getBody().getReview()); // from testReview
    }

    @Test
    void deleteReview_NotFound() {
        when(reviewRepository.existsById(999)).thenReturn(false);

        ResponseEntity<Void> response = reviewController.deleteReview(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteReview_Success() {
        when(reviewRepository.existsById(1)).thenReturn(true);

        ResponseEntity<Void> response = reviewController.deleteReview(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}