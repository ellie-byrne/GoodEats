package org.example.Mappers;

import org.example.Models.Review;
import org.example.DTOs.ReviewDTO;

public class ReviewMapper {

    public static ReviewDTO toDTO(Review review) {
        if (review == null) return null;

        return new ReviewDTO(
                review.getId(),
                review.getUserID(),
                review.getRestaurantID(),
                review.getDate(),
                review.getReview(),
                review.getRating(),
                review.isFavourite()
        );
    }

    public static Review fromDTO(ReviewDTO dto) {
        if (dto == null) return null;

        return new Review(
                dto.getId(),
                dto.getUserID(),
                dto.getRestaurantID(),
                dto.getDate(),
                dto.getReview(),
                dto.getRating(),
                dto.isFavourite()
        );
    }
}