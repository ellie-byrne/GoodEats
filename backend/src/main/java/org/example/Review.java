package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Reviews") // Updated to match the new collection name
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    private Integer id;
    private Integer userID;
    private Integer restaurantID;
    private Date date;
    private String review;
    private int rating;

    public Review(Integer userID, Integer restaurantID, Date date, String review, Integer rating) {
        this.userID = userID;
        this.restaurantID = restaurantID;
        this.date = date;
        this.review = review;
        this.rating = rating;
    }
}