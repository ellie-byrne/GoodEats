package org.example.DTOs;

import lombok.Data;

@Data
public class CreateReviewRequest {
    private Integer userID;
    private Integer restaurantID;
    private String review;
    private Integer rating;
    private boolean favourite;
}