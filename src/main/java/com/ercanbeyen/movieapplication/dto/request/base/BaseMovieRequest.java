package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.entity.enums.Genre;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseMovieRequest {
    private String title;
    private String language;
    private Integer releaseYear;
    private Double rating;
    private Genre genre;
    private String summary;
}
