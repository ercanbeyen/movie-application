package com.ercanbeyen.movieapplication.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@JsonFilter("nameFilter")
@Builder
public record ActorDto(
        Integer id, String name, String surname, String nationality,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate birthDate,
        Set<Integer> moviesPlayed, String biography) implements Serializable {

}
