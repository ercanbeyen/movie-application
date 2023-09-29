package com.ercanbeyen.movieapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record AudienceDto(
        Integer id, String name, String surname, String nationality,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthYear) implements Serializable {

}
