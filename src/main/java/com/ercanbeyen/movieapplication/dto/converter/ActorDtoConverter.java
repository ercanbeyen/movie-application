package com.ercanbeyen.movieapplication.dto.converter;

import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ActorDtoConverter {
    public ActorDto convert(Actor actor) {
        return ActorDto.builder()
                .name(actor.getName())
                .surname(actor.getSurname())
                .nationality(actor.getNationality())
                .birthYear(actor.getBirthYear())
                .moviesPlayed(
                        actor.getMoviesPlayed().stream()
                                .map(Movie::getId)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
