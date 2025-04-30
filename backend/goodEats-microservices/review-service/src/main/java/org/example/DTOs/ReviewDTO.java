// File: src/main/java/org/example/DTOs/ReviewDTO.java
package org.example.DTOs;

import java.util.Date;

public class ReviewDTO {
    private Integer id;
    private Integer userId;
    private Integer restaurantId;
    private Date date;
    private String review;
    private int rating;
    private boolean favourite;

    public ReviewDTO() {
    }

    public ReviewDTO(Integer id, Integer userId, Integer restaurantId, Date date, String review, int rating, boolean favourite) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.date = date;
        this.review = review;
        this.rating = rating;
        this.favourite = favourite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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