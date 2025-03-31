package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String borough;
    private String storePhoto;
    private String link;

    // Constructor without id for CSV import
    public Restaurant(String name, String category, String borough, String storePhoto, String link) {
        this.name = name;
        this.category = category;
        this.borough = borough;
        this.storePhoto = storePhoto;
        this.link = link;
    }

    // Explicit getters and setters to ensure they're available
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBorough() {
        return borough;
    }

    public void setBorough(String borough) {
        this.borough = borough;
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
}

