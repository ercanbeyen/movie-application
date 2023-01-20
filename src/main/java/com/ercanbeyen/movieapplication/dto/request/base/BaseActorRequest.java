package com.ercanbeyen.movieapplication.dto.request.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public class BaseActorRequest {
    private String name;
    private String surname;
    private String nationality;
    private LocalDate birthYear;
    private String biography;
}
