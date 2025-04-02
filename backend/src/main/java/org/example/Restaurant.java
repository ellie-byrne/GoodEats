package org.example;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "GoodEats")
public class Restaurant {
    @Id
    private Object id;

    private String name;
    private String type;
    private String borough;
    private String category;
    private String storePhoto;
    private String link;
    private String review;
    private List<Review> reviews;

    public Restaurant() {}

    public Restaurant(Object id, String name, String type, String borough,
                      String category, String storePhoto,
                      String link, String review, List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.borough = borough;
        this.category = category;
        this.storePhoto = storePhoto;
        this.link = link;
        this.review = review;
        this.reviews = reviews;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
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
        return borough;
    }

    public void setBorough(String borough) {
        this.borough = borough;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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