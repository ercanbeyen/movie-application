package com.ercanbeyen.movieapplication.option.filter;

public record CinemaFilteringOptions(
        String city, Boolean reservation_with_phone,
        Boolean threeD_animation, Boolean parking_place,
        Boolean air_conditioning, Boolean cafe_food) {

}
