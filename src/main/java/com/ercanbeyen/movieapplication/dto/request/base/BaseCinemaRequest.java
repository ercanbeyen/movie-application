package com.ercanbeyen.movieapplication.dto.request.base;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseCinemaRequest {
    @NotBlank(message = "Name should not be blank")
    private String name;
    @NotBlank(message = "Country should not be blank")
    private String country;
    @NotBlank(message = "City should not be blank")
    private String city;
    @NotBlank(message = "Address should not be blank")
    private String address;
    @NotBlank(message = "Contact number should not be blank")
    private String contactNumber;
    @NotNull(message = "Number of halls should not be null")
    private int numberOfHalls;
    @NotNull(message = "Reservation status should not be null")
    private boolean reservation_with_phone;
    @NotNull(message = "3-D Animation status should not be null")
    private boolean threeD_animation;
    @NotNull(message = "Parking place status should not be null")
    private boolean parking_place;
    @NotNull(message = "Air conditioning status should not be null")
    private boolean air_conditioning;
    @NotNull(message = "Cafe & Food status should not be null")
    private boolean cafe_food;
}
