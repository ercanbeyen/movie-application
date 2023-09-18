package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.annotation.ImdbIdRequest;
import com.ercanbeyen.movieapplication.constant.enums.Genre;
import com.ercanbeyen.movieapplication.constant.message.ValidationMessages;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseMovieRequest {
    @ImdbIdRequest
    @NotBlank(message = "ImdbId " + ValidationMessages.SHOULD_NOT_BLANK)
    private String imdbId;
    @NotBlank(message = "Title" + ValidationMessages.SHOULD_NOT_BLANK)
    private String title;
    Integer directorId;
    @NotBlank(message = "Language" + ValidationMessages.SHOULD_NOT_BLANK)
    private String language;
    @NotNull(message = "Release year" + ValidationMessages.SHOULD_NOT_NULL)
    private Integer releaseYear;
    @Range(min = 1, max = 5, message = "Rating should be between {min} and {max}")
    @NotNull(message = "Rating" + ValidationMessages.SHOULD_NOT_NULL)
    private Double rating;
    private Genre genre;
    private String summary;
}
