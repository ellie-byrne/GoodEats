package org.example;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "restaurants")
public class Restaurant {
    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;

    private String Borough;
    private String Name;
    private String Category;

    //for future use
    private String storePhoto;
    private String link;
    private String review;
    private List<Review> reviews;

    public Restaurant() {}

    public Restaurant(ObjectId id, String Name, String Category, String Borough, String storePhoto,
                      String link, String review, List<Review> reviews) {
        this.id = id;
        this.Name = Name;
        this.Category = Category;
        this.Borough = Borough;
        this.storePhoto = storePhoto;
        this.link = link;
        this.review = review;
        this.reviews = reviews;
    }

    // Getters and setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getBorough() {
        return Borough;
    }

    public void setBorough(String borough) {
        Borough = borough;
    }

    public String getStorePhoto() {
        return storePhoto;
    }

    public void setStorePhoto(String storePhoto) {
        this.storePhoto = storePhoto;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getIdAsString() {
        if (id != null) {
            return id.toString();
        }
        return null;
    }
}