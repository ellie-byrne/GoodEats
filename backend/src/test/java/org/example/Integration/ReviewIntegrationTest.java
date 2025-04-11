// src/test/java/org/example/ReviewIntegrationTest.java
package org.example.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Models.Review;
import org.example.Respositories.ReviewRepository;
import org.example.Services.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private ReviewService reviewService;

    @Test
    void getReviewsForRestaurant() throws Exception {
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

        when(reviewRepository.findByRestaurantID(1)).thenReturn(Arrays.asList(review1, review2));
        when(reviewRepository.findByRestaurantID(999)).thenReturn(new ArrayList<>());

        // Act & Assert - Success case
        mockMvc.perform(get("/api/restaurants/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].review", is("Great food!")))
                .andExpect(jsonPath("$[0].rating", is(5)))
                .andExpect(jsonPath("$[1].review", is("Good service!")))
                .andExpect(jsonPath("$[1].rating", is(4)));

        // Act & Assert - Empty list case
        mockMvc.perform(get("/api/restaurants/999/reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createReview() throws Exception {
        // Arrange
        Map<String, Object> reviewPayload = new HashMap<>();
        reviewPayload.put("userID", 1);
        reviewPayload.put("restaurantID", 1);
        reviewPayload.put("review", "Great food!");
        reviewPayload.put("rating", 5);

        Review createdReview = new Review();
        createdReview.setId(1);
        createdReview.setUserID(1);
        createdReview.setRestaurantID(1);
        createdReview.setReview("Great food!");
        createdReview.setRating(5);
        createdReview.setDate(new Date());

        when(reviewService.createReview(anyInt(), anyInt(), any(), anyInt())).thenReturn(createdReview);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userID", is(1)))
                .andExpect(jsonPath("$.restaurantID", is(1)))
                .andExpect(jsonPath("$.review", is("Great food!")))
                .andExpect(jsonPath("$.rating", is(5)));
    }
}