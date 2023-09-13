package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.annotation.ImdbIdRequest;
import com.ercanbeyen.movieapplication.constant.enums.Genre;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseMovieRequest {
    @ImdbIdRequest
    @NotBlank(message = "ImdbId " + ResponseMessages.SHOULD_NOT_BLANK)
    private String imdbId;
    @NotBlank(message = "Title" + ResponseMessages.SHOULD_NOT_BLANK)
    private String title;
    Integer directorId;
    @NotBlank(message = "Language" + ResponseMessages.SHOULD_NOT_BLANK)
    private String language;
    @NotNull(message = "Release year" + ResponseMessages.SHOULD_NOT_NULL)
    private Integer releaseYear;
    @Min(value = 0, message = "Minimum rating for movie is 0")
    @Max(value = 5, message = "Maximum rating for movie is 5")
    @NotNull(message = "Rating" + ResponseMessages.SHOULD_NOT_NULL)
    private Double rating;
    private Genre genre;
    private String summary;
}
