package com.ercanbeyen.movieapplication.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record CinemaDto(
        String id, String name, String country, String city,
        String address, String contactNumber, int numberOfHalls,
        boolean reservation_with_phone, boolean threeD_animation,
        boolean parking_place, boolean air_conditioning, boolean cafe_food) implements Serializable {

}
