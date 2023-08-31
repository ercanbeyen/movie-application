package com.ercanbeyen.movieapplication.dto.option.filter;

import lombok.Data;

@Data
public class CinemaFilteringOptions {
    String city;
    Boolean reservation_with_phone;
    Boolean threeD_animation;
    Boolean parking_place;
    Boolean air_conditioning;
    Boolean cafe_food;
}
