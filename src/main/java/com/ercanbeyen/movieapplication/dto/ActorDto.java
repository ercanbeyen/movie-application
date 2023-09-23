package com.ercanbeyen.movieapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Builder
public record ActorDto(
        Integer id, String name, String surname, String nationality,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthYear,
        Set<Integer> moviesPlayed) implements Serializable {

}
