// File: src/main/java/org/example/DTOs/UpdateReviewRequest.java
package org.example.DTOs;

public class UpdateReviewRequest {
    private String review;
    private Integer rating;
    private Boolean favourite;

    public UpdateReviewRequest() {
    }

    public UpdateReviewRequest(String review, Integer rating, Boolean favourite) {
        this.review = review;
        this.rating = rating;
        this.favourite = favourite;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }
}