package com.ercanbeyen.movieapplication.dto.request.update;

import com.ercanbeyen.movieapplication.dto.request.base.BaseMovieRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateMovieRequest extends BaseMovieRequest {
    Set<Integer> actorIds;
}
