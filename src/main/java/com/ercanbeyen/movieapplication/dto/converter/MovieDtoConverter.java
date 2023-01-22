package com.ercanbeyen.movieapplication.dto.converter;

import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MovieDtoConverter {
    public MovieDto convert(Movie movie) {
        return MovieDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .directorId(movie.getDirector().getId())
                .actorsIds(
                        movie.getActors().stream()
                                .map(Actor::getId)
                                .collect(Collectors.toSet())
                )
                .language(movie.getLanguage())
                .releaseYear(movie.getReleaseYear())
                .build();
    }
}
