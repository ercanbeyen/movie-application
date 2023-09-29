package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.message.ValidationMessages;
import com.ercanbeyen.movieapplication.constant.names.FieldNames;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseCinemaRequest {
    @NotBlank(message = FieldNames.NAME + ValidationMessages.SHOULD_NOT_BLANK)
    private String name;
    @NotBlank(message = "Country" + ValidationMessages.SHOULD_NOT_BLANK)
    private String country;
    @NotBlank(message = "City" + ValidationMessages.SHOULD_NOT_BLANK)
    private String city;
    @NotBlank(message = "Address" + ValidationMessages.SHOULD_NOT_BLANK)
    private String address;
    @NotBlank(message = "Contact number" + ValidationMessages.SHOULD_NOT_BLANK)
    private String contactNumber;
    @Min(value = 1, message = "Minimum value is {value}")
    @NotNull(message = "Number of halls" + ValidationMessages.SHOULD_NOT_NULL)
    private int numberOfHalls;
    @NotNull(message = "Reservation status" + ValidationMessages.SHOULD_NOT_NULL)
    private boolean reservation_with_phone;
    @NotNull(message = "3-D Animation status" + ValidationMessages.SHOULD_NOT_NULL)
    private boolean threeD_animation;
    @NotNull(message = "Parking place status" + ValidationMessages.SHOULD_NOT_NULL)
    private boolean parking_place;
    @NotNull(message = "Air conditioning status" + ValidationMessages.SHOULD_NOT_NULL)
    private boolean air_conditioning;
    @NotNull(message = "Cafe & Food status" + ValidationMessages.SHOULD_NOT_NULL)
    private boolean cafe_food;
}
