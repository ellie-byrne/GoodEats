// File: src/main/java/org/example/DTOs/RestaurantDTO.java
package org.example.DTOs;

public class RestaurantDTO {
    private Integer id;
    private String name;
    private String cuisine;
    private String location;
    private String priceRange;
    private String imageUrl;
    private Double averageRating;

    public RestaurantDTO() {
    }

    public RestaurantDTO(Integer id, String name, String cuisine, String location, String priceRange, String imageUrl, Double averageRating) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.location = location;
        this.priceRange = priceRange;
        this.imageUrl = imageUrl;
        this.averageRating = averageRating;
    }

    public Integer getId() {
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

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}