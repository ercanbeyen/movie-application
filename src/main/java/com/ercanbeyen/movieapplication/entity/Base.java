package com.ercanbeyen.movieapplication.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
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
public sealed abstract class Base implements Serializable permits Actor, Director, Audience {
    @Column(length = 100)
    private String name;
    @Column(length = 100)
    private String surname;
    @Column(length = 100)
    private String nationality;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthYear;
    @Column(length = 1024)
    private String biography;
}
