package org.example.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "GoodEats") // Ensure this matches the collection name in MongoDB
public class Restaurant {
    @Id
    private Integer id;

    private String name;
    private String type;
    private String Borough; // Updated to match the new format
    private String storePhoto;
    private List<Review> reviews; // This can be updated to link to the new Reviews collection

    // Default constructor
    public Restaurant() {}

    // Parameterized constructor
    public Restaurant(Integer id, String name, String type, String Borough,
                      String storePhoto, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.Borough = Borough;
        this.storePhoto = storePhoto;
        this.reviews = reviews;
    }

    // Getters and Setters
    public Object getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBorough() {
        return Borough;
    }

    public void setBorough(String Borough) {
        this.Borough = Borough;
    }

    public String getStorePhoto() {
        return storePhoto;
    }

    public void setStorePhoto(String storePhoto) {
        this.storePhoto = storePhoto;
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