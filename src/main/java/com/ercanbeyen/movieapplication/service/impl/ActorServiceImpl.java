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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Cacheable(value = "actors", key = "#id", unless = "#result.moviesPlayed.size() < 2")
    @Override
    public ActorDto getActor(Integer id) {
        log.info("Fetch actor from database");
        Actor actorInDb = getActorById(id);
        return actorDtoConverter.convert(actorInDb);
    }

    @CacheEvict(value = "actors", allEntries = true)
    @Override
    public ActorDto updateActor(Integer id, UpdateActorRequest request) {
        log.info("Update actor operation is starting");
        Actor actorInDb = getActorById(id);

        actorInDb.setName(request.getName());
        actorInDb.setSurname(request.getSurname());
        actorInDb.setNationality(request.getNationality());
        actorInDb.setBirthYear(request.getBirthYear());
        actorInDb.setBiography(request.getBiography());

        return actorDtoConverter.convert(actorRepository.save(actorInDb));
    }

    @CacheEvict(value = "actors", key = "#id")
    @Override
    public String deleteActor(Integer id) {
        log.info("Delete actor operation is starting");
        actorRepository.deleteById(id);
        return "Actor " + id + " is successfully deleted";
    }

    @Cacheable(value = "actors")
    @Override
    public List<ActorDto> getMostPopularActors() {
        log.info("Fetch actors from database");
        List<Actor> actors = actorRepository.findAll();
        int numberOfMovies = 2;

        return actors.stream()
                .filter(actor -> actor.getMoviesPlayed().size() >= numberOfMovies)
                .map(actorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActorDto> searchActors(String fullName) {
        List<Actor> actors = actorRepository.findByFullName(fullName);
        return actors.stream()
                .map(actorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    private Actor getActorById(Integer id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Actor " + id + " is not found"));
    }
}
