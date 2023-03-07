package com.ercanbeyen.movieapplication.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class CinemaDto implements Serializable {
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
