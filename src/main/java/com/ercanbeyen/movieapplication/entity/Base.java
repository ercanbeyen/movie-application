package com.ercanbeyen.movieapplication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@MappedSuperclass
public abstract class Base implements Serializable {
    private String name;
    private String surname;
    private String nationality;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthYear;
    private String biography;
}
