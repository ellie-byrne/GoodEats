// File: src/main/java/org/example/Mappers/ReviewMapper.java
package org.example.Mappers;

import org.example.DTOs.ReviewDTO;
import org.example.Models.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {
    public ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getUserId(),
                review.getRestaurantId(),
                review.getDate(),
                review.getReview(),
                review.getRating(),
                review.isFavourite()
        );
    }
}