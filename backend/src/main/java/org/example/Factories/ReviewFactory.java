package org.example.Factories;

import org.example.Models.Review;

import java.util.Date;

public class ReviewFactory {
    public static Review create(Integer id, Integer userID, Integer restaurantID, String reviewText, int rating, boolean favourite) {
        return new Review(id, userID, restaurantID, new Date(), reviewText, rating, favourite);
    }
}