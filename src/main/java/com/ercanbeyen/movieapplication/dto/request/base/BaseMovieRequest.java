package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.enums.Genre;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseMovieRequest {
    @NotBlank(message = "title should not be blank")
    private String title;
    @NotBlank(message = "language should not be blank")
    private String language;
    @NotNull(message = "release year should not be null")
    private Integer releaseYear;
    @NotNull(message = "rating should not be null")
    private Double rating;
    private Genre genre;
    private String summary;
}
