package com.ercanbeyen.movieapplication.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
@ToString
public class Base implements Serializable {
    private String name;
    private String surname;
    private String nationality;
    private LocalDate birthYear;
    private String biography;
}
