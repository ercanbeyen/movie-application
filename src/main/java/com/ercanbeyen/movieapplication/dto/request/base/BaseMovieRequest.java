package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.enums.Genre;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseMovieRequest {
    @NotBlank(message = "Title" + ResponseMessages.SHOULD_NOT_BLANK)
    private String title;
    Integer directorId;
    @NotBlank(message = "Language" + ResponseMessages.SHOULD_NOT_BLANK)
    private String language;
    @NotNull(message = "Release year" + ResponseMessages.SHOULD_NOT_NULL)
    private Integer releaseYear;
    @NotNull(message = "Rating" + ResponseMessages.SHOULD_NOT_NULL)
    private Double rating;
    private Genre genre;
    private String summary;
}
