// File: src/main/java/org/example/DTOs/CreateReviewRequest.java
package org.example.DTOs;

public class CreateReviewRequest {
    private Integer userId;
    private Integer restaurantId;
    private String review;
    private int rating;
    private boolean favourite;

    public CreateReviewRequest() {
    }

    public CreateReviewRequest(Integer userId, Integer restaurantId, String review, int rating, boolean favourite) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.review = review;
        this.rating = rating;
        this.favourite = favourite;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}