package com.ercanbeyen.movieapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class ActorDto implements Serializable {
    private Integer id;
    private String name;
    private String surname;
    private String nationality;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthYear;
    private String biography;
    private Set<Integer> moviesPlayed;
}
