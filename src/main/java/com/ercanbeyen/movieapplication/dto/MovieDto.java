package com.ercanbeyen.movieapplication.dto;

import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.entity.enums.Genre;
import lombok.Builder;
import lombok.Data;


import java.util.Set;

@Data
@Builder
public class MovieDto {
    private String title;
    private String language;
    private Integer releaseYear;
    private Double rating;
    private Genre genre;
    private String summary;
    private Set<Actor> actors;
    private Director director;
}
