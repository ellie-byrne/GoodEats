package org.example.DTOs;

public class RestaurantDTO {
    private Integer id;
    private String name;
    private String type;
    private String borough;
    private String storePhoto;
    private String postcode;

    public RestaurantDTO() {}

    public RestaurantDTO(Integer id, String name, String type, String borough, String storePhoto, String postcode) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.borough = borough;
        this.storePhoto = storePhoto;
        this.postcode = postcode;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBorough() { return borough; }
    public void setBorough(String borough) { this.borough = borough; }

    public String getStorePhoto() { return storePhoto; }
    public void setStorePhoto(String storePhoto) { this.storePhoto = storePhoto; }

    public String getPostcode() {return postcode;}
    public void setPostcode(String postcode) {this.postcode = postcode;}
}