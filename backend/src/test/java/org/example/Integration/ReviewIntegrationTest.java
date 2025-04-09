package org.example.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Models.Review;
import org.example.Repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        reviewRepository.deleteAll();

        Review review = new Review();
        review.setId(1);
        review.setUserID(101);
        review.setRestaurantID(201);
        review.setReview("Great food and service!");
        review.setRating(5);
        review.setDate(new Date());
        review.setFavourite(false);

        reviewRepository.save(review);
    }

    @Test
    public void testGetReviewsForRestaurant() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/restaurants/201/reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("Great food and service!"));
        assertTrue(content.contains("5"));
    }

    @Test
    public void testCreateReview() throws Exception {
        Map<String, Object> reviewPayload = new HashMap<>();
        reviewPayload.put("userID", 102);
        reviewPayload.put("restaurantID", 202);
        reviewPayload.put("review", "Excellent food!");
        reviewPayload.put("rating", 5);
        reviewPayload.put("favourite", true);

        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("Excellent food!"));
        assertTrue(content.contains("5"));
        assertTrue(content.contains("true"));
    }

    @Test
    public void testUpdateReview() throws Exception {
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("review", "Updated review text");
        updatePayload.put("rating", 4);

        MvcResult result = mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("Updated review text"));
        assertTrue(content.contains("4"));
    }

    @Test
    public void testUpdateFavourite() throws Exception {
        Map<String, Object> favouritePayload = new HashMap<>();
        favouritePayload.put("favourite", true);

        MvcResult result = mockMvc.perform(put("/api/reviews/1/favourite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(favouritePayload)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("true"));
    }

    @Test
    public void testDeleteReview() throws Exception {
        mockMvc.perform(delete("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify the review is deleted
        mockMvc.perform(get("/api/restaurants/201/reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}