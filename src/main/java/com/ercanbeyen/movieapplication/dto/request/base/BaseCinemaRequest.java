package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseCinemaRequest {
    @NotBlank(message = "Name" + ResponseMessages.SHOULD_NOT_BLANK)
    private String name;
    @NotBlank(message = "Country" + ResponseMessages.SHOULD_NOT_BLANK)
    private String country;
    @NotBlank(message = "City" + ResponseMessages.SHOULD_NOT_BLANK)
    private String city;
    @NotBlank(message = "Address" + ResponseMessages.SHOULD_NOT_BLANK)
    private String address;
    @NotBlank(message = "Contact number" + ResponseMessages.SHOULD_NOT_BLANK)
    private String contactNumber;
    @NotNull(message = "Number of halls" + ResponseMessages.SHOULD_NOT_NULL)
    private int numberOfHalls;
    @NotNull(message = "Reservation status" + ResponseMessages.SHOULD_NOT_NULL)
    private boolean reservation_with_phone;
    @NotNull(message = "3-D Animation status" + ResponseMessages.SHOULD_NOT_NULL)
    private boolean threeD_animation;
    @NotNull(message = "Parking place status" + ResponseMessages.SHOULD_NOT_NULL)
    private boolean parking_place;
    @NotNull(message = "Air conditioning status" + ResponseMessages.SHOULD_NOT_NULL)
    private boolean air_conditioning;
    @NotNull(message = "Cafe & Food status" + ResponseMessages.SHOULD_NOT_NULL)
    private boolean cafe_food;
}
