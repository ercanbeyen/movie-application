package com.ercanbeyen.movieapplication.dto.request.create;

import com.ercanbeyen.movieapplication.dto.request.base.BaseMovieRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateMovieRequest extends BaseMovieRequest {
    @NotNull(message = "Director id should not be null")
    Integer directorId;
}
