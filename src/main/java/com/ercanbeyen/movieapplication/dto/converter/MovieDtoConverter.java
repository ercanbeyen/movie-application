package com.ercanbeyen.movieapplication.dto.converter;

import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieDtoConverter {
    public MovieDto convert(Movie movie) {
        return MovieDto.builder()
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .director(movie.getDirector())
                .actors(movie.getActors())
                .language(movie.getLanguage())
                .releaseYear(movie.getReleaseYear())
                .build();
    }
}
