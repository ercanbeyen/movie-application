package com.ercanbeyen.movieapplication.dto.option.filter;

import com.ercanbeyen.movieapplication.entity.enums.Genre;
import lombok.Data;

@Data
public class MovieFilteringOptions {
    String language;
    Genre genre;
    Integer year;
    Integer limit;
}
