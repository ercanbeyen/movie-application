package com.ercanbeyen.movieapplication.dto.request.update;

import com.ercanbeyen.movieapplication.dto.request.base.BaseActorRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateActorRequest extends BaseActorRequest {
    private Set<Integer> moviesPlayed;
}
