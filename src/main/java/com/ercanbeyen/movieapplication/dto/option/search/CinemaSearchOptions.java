package com.ercanbeyen.movieapplication.dto.option.search;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CinemaSearchOptions {
    @NotNull(message = "Reservation phone should not be null")
    Boolean reservation_with_phone;
    @NotNull(message = "3D Animation should not be null")
    Boolean threeD_animation;
    @NotNull(message = "Parking place should not be null")
    Boolean parking_place;
    @NotNull(message = "Air conditioning should not be null")
    Boolean air_conditioning;
    @NotNull(message = "Cafe food should not be null")
    Boolean cafe_food;
}
