package com.ercanbeyen.movieapplication.document;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "cinema")
public class Cinema {
    @Id
    private String id;
    private String name;
    private String country;
    private String city;
    private String address;
    private String contactNumber;
    private int numberOfHalls;
    private boolean reservation_with_phone;
    private boolean threeD_animation;
    private boolean parking_place;
    private boolean air_conditioning;
    private boolean cafe_food;
}
