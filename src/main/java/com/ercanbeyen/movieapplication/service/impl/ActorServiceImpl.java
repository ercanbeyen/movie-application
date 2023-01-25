package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.converter.ActorDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.exception.EntityNotFound;
import com.ercanbeyen.movieapplication.repository.ActorRepository;
import com.ercanbeyen.movieapplication.service.ActorService;
import com.ercanbeyen.movieapplication.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final ActorDtoConverter actorDtoConverter;
    private final MovieService movieService;

    @Override
    public ActorDto createActor(CreateActorRequest request) {
        Actor newActor = Actor.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .moviesPlayed(new HashSet<>())
                .build();

        return actorDtoConverter.convert(actorRepository.save(newActor));
    }

    @Override
    public List<ActorDto> getActors(String nationality, Integer year, Integer movieId) {
        List<Actor> actors = actorRepository.findAll();

        if (!StringUtils.isBlank(nationality)) {
            actors = actors.stream()
                    .filter(actor -> actor.getNationality().equals(nationality))
                    .collect(Collectors.toList());
        }

        if (year != null) {
            actors = actors.stream()
                    .filter(actor ->  actor.getBirthYear().getYear() == year)
                    .collect(Collectors.toList());
        }

        if (movieId != null) {
            Movie movie = movieService.getMovieById(movieId);
            actors = actors.stream()
                    .filter(actor -> actor.getMoviesPlayed().contains(movie))
                    .collect(Collectors.toList());
        }

        return actors.stream()
                .map(actorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public ActorDto getActor(Integer id) {
        Actor actorInDb = getActorById(id);
        return actorDtoConverter.convert(actorInDb);
    }

    @Override
    public ActorDto updateActor(Integer id, UpdateActorRequest request) {
        Actor actorInDb = getActorById(id);

        actorInDb.toBuilder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .build();

        return actorDtoConverter.convert(actorRepository.save(actorInDb));
    }

    @Override
    public String deleteActor(Integer id) {
        actorRepository.deleteById(id);
        return "Actor " + id + " is successfully deleted";
    }

    private Actor getActorById(Integer id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Actor " + id + " is not found"));
    }
}
