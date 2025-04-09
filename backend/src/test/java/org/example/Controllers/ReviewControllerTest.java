// src/test/java/org/example/Controllers/ReviewControllerTest.java
package org.example.Controllers;

import org.example.Models.Review;
import org.example.Repositories.ReviewRepository;
import org.example.Services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReviewControllerTest {

    private ReviewController reviewController;
    private TestReviewRepository reviewRepository;
    private TestReviewService reviewService;

    @BeforeEach
    public void setup() {
        reviewRepository = new TestReviewRepository();
        reviewService = new TestReviewService();
        reviewController = new ReviewController();

        // Set the repository and service using reflection
        try {
            java.lang.reflect.Field repoField = ReviewController.class.getDeclaredField("reviewRepository");
            repoField.setAccessible(true);
            repoField.set(reviewController, reviewRepository);

            java.lang.reflect.Field serviceField = ReviewController.class.getDeclaredField("reviewService");
            serviceField.setAccessible(true);
            serviceField.set(reviewController, reviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testGetReviewsForRestaurant_Success() {
        // Arrange
        Review review = new Review();
        review.setId(1);
        review.setUserID(101);
        review.setRestaurantID(201);
        review.setReview("Great food and service!");
        review.setRating(5);
        review.setDate(new Date());
        reviewRepository.save(review);

        // Make sure the repository returns the review for restaurant 201
        reviewRepository.setReviewsForRestaurant(201, Collections.singletonList(review));

        // Act
        ResponseEntity<List<Review>> response = reviewController.getReviewsForRestaurant(201);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Great food and service!", response.getBody().get(0).getReview());
    }

    @Test
    public void testGetReviewsForRestaurant_EmptyList() {
        // Act
        ResponseEntity<List<Review>> response = reviewController.getReviewsForRestaurant(999);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    public void testCreateReview_Success() {
        // Arrange
        Map<String, Object> reviewPayload = new HashMap<>();
        reviewPayload.put("userID", 101);
        reviewPayload.put("restaurantID", 201);
        reviewPayload.put("review", "Great food and service!");
        reviewPayload.put("rating", 5);

        Review createdReview = new Review();
        createdReview.setId(1);
        createdReview.setUserID(101);
        createdReview.setRestaurantID(201);
        createdReview.setReview("Great food and service!");
        createdReview.setRating(5);
        createdReview.setDate(new Date());

        reviewService.setReviewToReturn(createdReview);
        reviewService.setCreateReviewStatus(HttpStatus.CREATED); // Set the expected status

        // Act
        ResponseEntity<Review> response = reviewController.createReview(reviewPayload);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Great food and service!", response.getBody().getReview());
    }

    // Other test methods remain the same

    // Improved test repository implementation
    private static class TestReviewRepository implements ReviewRepository {
        private final Map<Integer, Review> reviews = new HashMap<>();
        private Map<Integer, List<Review>> reviewsByRestaurantId = new HashMap<>();

        public void setReviewsForRestaurant(int restaurantId, List<Review> restaurantReviews) {
            reviewsByRestaurantId.put(restaurantId, restaurantReviews);
        }

        @Override
        public List<Review> findByRestaurantID(int restaurantID) {
            return reviewsByRestaurantId.getOrDefault(restaurantID, new ArrayList<>());
        }

        @Override
        public List<Review> findByRestaurantID(Integer restaurantID) {
            return List.of();
        }

        @Override
        public <S extends Review> S save(S entity) {
            reviews.put(entity.getId(), entity);
            return entity;
        }

        @Override
        public <S extends Review> List<S> saveAll(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public Optional<Review> findById(Integer integer) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(Integer integer) {
            return false;
        }

        @Override
        public List<Review> findAll() {
            return List.of();
        }

        @Override
        public Iterable<Review> findAllById(Iterable<Integer> integers) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Integer integer) {

        }

        @Override
        public void delete(Review entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Integer> integers) {

        }

        @Override
        public void deleteAll(Iterable<? extends Review> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<Review> findAll(Sort sort) {
            return List.of();
        }

        @Override
        public Page<Review> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Review> S insert(S entity) {
            return null;
        }

        @Override
        public <S extends Review> List<S> insert(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public <S extends Review> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Review> List<S> findAll(Example<S> example) {
            return List.of();
        }

        @Override
        public <S extends Review> List<S> findAll(Example<S> example, Sort sort) {
            return List.of();
        }

        @Override
        public <S extends Review> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Review> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Review> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Review, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        // Other methods remain the same
    }

    // Improved test service implementation
    private static class TestReviewService extends ReviewService {
        private Review reviewToReturn;
        private HttpStatus createReviewStatus = HttpStatus.CREATED;

        public void setReviewToReturn(Review review) {
            this.reviewToReturn = review;
        }

        public void setCreateReviewStatus(HttpStatus status) {
            this.createReviewStatus = status;
        }

        @Override
        public Review createReview(int userID, int restaurantID, String review, int rating) {
            return reviewToReturn;
        }

        @Override
        public Review updateReview(int id, String review, Integer rating) {
            return null;
        }


        @Override
        public Review updateReview(Integer id, String review, Integer rating) {
            return reviewToReturn;
        }
    }
}