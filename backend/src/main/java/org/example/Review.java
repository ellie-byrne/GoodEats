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
    private ObjectId id;
    private String userID; // New field
    private String restaurantID; // New field
    private Date date; // New field
    private String review; // New field
    private int rating; // New field

    public Review(String userID, String restaurantID, Date date, String review, int rating) {
        this.userID = userID;
        this.restaurantID = restaurantID;
        this.date = date;
        this.review = review;
        this.rating = rating;
    }
}