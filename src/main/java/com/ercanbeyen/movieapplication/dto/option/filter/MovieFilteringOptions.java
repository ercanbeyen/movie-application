package com.ercanbeyen.movieapplication.dto.option.filter;

import com.ercanbeyen.movieapplication.constant.enums.Genre;

import java.util.List;

public record MovieFilteringOptions(String language, List<Genre> genres, Integer releaseYear) {

}
