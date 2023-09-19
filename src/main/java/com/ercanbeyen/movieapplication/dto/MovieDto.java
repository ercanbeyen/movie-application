package com.ercanbeyen.movieapplication.dto;

import com.ercanbeyen.movieapplication.constant.enums.Genre;
import lombok.Builder;

import java.io.Serializable;
import java.util.Set;

@Builder
public record MovieDto(
        Integer id, String imdbId, String title, String language,
        Integer releaseYear, Double rating, Genre genre, String summary,
        Set<Integer> actorsIds, Integer directorId) implements Serializable {

}