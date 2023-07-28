package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.OrderBy;
import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.converter.ActorDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.ActorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.exception.EntityNotFound;
import com.ercanbeyen.movieapplication.repository.ActorRepository;
import com.ercanbeyen.movieapplication.service.ActorService;
import com.ercanbeyen.movieapplication.util.CustomPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<ActorDto> getActors(ActorFilteringOptions filteringOptions, OrderBy orderBy) {
        List<Actor> actors = actorRepository.findAll();

        if (filteringOptions.getMovieId() != null) {
            actors = actors
                    .stream()
                    .filter(actor -> actor.getMoviesPlayed()
                            .stream()
                            .map(Movie::getId)
                            .anyMatch(id -> filteringOptions.getMovieId().intValue() == id.intValue()))
                    .toList();
        }

        if (!StringUtils.isBlank(filteringOptions.getNationality())) {
            actors = actors.stream()
                    .filter(actor -> actor.getNationality().equals(filteringOptions.getNationality()))
                    .collect(Collectors.toList());
        }

        if (filteringOptions.getYear() != null) {
            actors = actors.stream()
                    .filter(actor ->  actor.getBirthYear().getYear() == filteringOptions.getYear())
                    .collect(Collectors.toList());
        }

        if (orderBy != null) {
            actors = actors.stream()
                    .sorted((actor1, actor2) -> {
                        int numberOfMoviesPlayed1 = actor1.getMoviesPlayed().size();
                        int numberOfMoviesPlayed2 = actor2.getMoviesPlayed().size();

                        if (orderBy == OrderBy.DESC) {
                            return Integer.compare(numberOfMoviesPlayed2, numberOfMoviesPlayed1);
                        } else {
                            return Integer.compare(numberOfMoviesPlayed1, numberOfMoviesPlayed2);
                        }
                    })
                    .toList();

            log.info("Actors are sorted by number of movies played");

            if (filteringOptions.getLimit() != null) {
                actors = actors.stream()
                        .limit(filteringOptions.getLimit())
                        .toList();
                log.info("Top {} actors are selected", filteringOptions.getLimit());
            }
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
                .toList();
    }

    @Override
    public CustomPage<ActorDto, Actor> getActors(Pageable pageable) {
                Page<Actor> page = actorRepository.findAll(pageable);
        List<ActorDto> actorDtoList = page.getContent()
                .stream()
                .map(actorDtoConverter::convert)
                .toList();

        return new CustomPage<>(page, actorDtoList);

    }

    private Actor getActorById(Integer id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Actor " + id + " is not found"));
    }
}
