package org.example.DTOs;

import lombok.Data;

@Data
public class UpdateReviewRequest {
    private String review;
    private Integer rating;
    private Boolean favourite;
}