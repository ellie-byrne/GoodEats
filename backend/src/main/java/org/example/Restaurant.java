package org.example;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "restaurants")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Restaurant {
    @Id
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id; // Changed from _id
    private String name;
    private String type;
    private String link;
    private String storePhoto;
    private String review;
    private List<Review> reviews;
}