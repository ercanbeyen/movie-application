package com.ercanbeyen.movieapplication.dto.converter;

import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class DirectorDtoConverter {
    public DirectorDto convert(Director director) {
        return DirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .surname(director.getSurname())
                .nationality(director.getNationality())
                .birthYear(director.getBirthYear())
                .biography(director.getBiography())
                .moviesDirected(
                        director.getMoviesDirected().stream()
                                .map(Movie::getId)
                                .toList())
                .build();
    }
}
