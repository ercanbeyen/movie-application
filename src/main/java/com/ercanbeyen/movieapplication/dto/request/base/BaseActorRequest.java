package com.ercanbeyen.movieapplication.dto.request.base;

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseActorRequest {
    @NotBlank(message = "name should not be blank")
    private String name;
    @NotBlank(message = "surname should not be blank")
    private String surname;
    @NotBlank(message = "nationality should not be blank")
    private String nationality;
    private LocalDate birthYear;
    private String biography;
}
