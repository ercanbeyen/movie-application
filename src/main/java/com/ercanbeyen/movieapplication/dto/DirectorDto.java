package com.ercanbeyen.movieapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder
public record DirectorDto(
        Integer id, String name,
        String surname, String nationality,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
        String biography, List<Integer> moviesDirected) implements Serializable {

}
