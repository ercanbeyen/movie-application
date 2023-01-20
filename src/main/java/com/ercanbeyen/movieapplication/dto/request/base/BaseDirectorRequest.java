package com.ercanbeyen.movieapplication.dto.request.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class BaseDirectorRequest {
    private String name;
    private String surname;
    private String nationality;
    private LocalDate birthYear;
    private String biography;
}
