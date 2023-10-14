package com.ercanbeyen.movieapplication.option.search;

import com.ercanbeyen.movieapplication.constant.message.ValidationMessages;
import jakarta.validation.constraints.NotNull;

public record CinemaSearchOptions(
        @NotNull(message = "Reservation" + ValidationMessages.SHOULD_NOT_NULL) Boolean reservation_with_phone,
        @NotNull(message = "3D Animation" + ValidationMessages.SHOULD_NOT_NULL) Boolean threeD_animation,
        @NotNull(message = "Parking place" + ValidationMessages.SHOULD_NOT_NULL) Boolean parking_place,
        @NotNull(message = "Air conditioning" + ValidationMessages.SHOULD_NOT_NULL) Boolean air_conditioning,
        @NotNull(message = "Cafe & Food" + ValidationMessages.SHOULD_NOT_NULL) Boolean cafe_food) {

}
