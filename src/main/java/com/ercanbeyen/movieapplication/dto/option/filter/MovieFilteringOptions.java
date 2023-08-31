package com.ercanbeyen.movieapplication.dto.option.filter;

import com.ercanbeyen.movieapplication.constant.enums.Genre;
import lombok.Data;

import java.util.List;

@Data
public class MovieFilteringOptions {
    String language;
    List<Genre> genres;
    Integer releaseYear;
}
