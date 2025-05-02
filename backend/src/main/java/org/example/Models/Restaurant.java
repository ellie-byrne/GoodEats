package org.example.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Document(collection = "GoodEats")
public class Restaurant {
    @Id
    private Integer id;
    private String name;
    private String type;
    @JsonProperty("Borough")
    private String Borough;
    private String storePhoto;
    private String postcode;

    public Restaurant() {}

    public Restaurant(Integer id, String name, String type, String Borough,
                      String storePhoto, String postcode,List<Review> reviews) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.Borough = Borough;
        this.storePhoto = storePhoto;
        this.postcode = postcode;
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

    public String getPostcode() {return postcode;}

    public void setPostcode(String postcode) {this.postcode = postcode;}

    public String getIdAsString() {
        if (id != null) {
            return id.toString();
        }
        return null;
    }
}