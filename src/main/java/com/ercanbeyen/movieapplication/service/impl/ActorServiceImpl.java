package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.ActionNames;
import com.ercanbeyen.movieapplication.constant.message.EntityNames;
import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
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
        log.info(String.format(LogMessages.STARTED, "createActor"));

        Actor newActor = Actor.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .moviesPlayed(new HashSet<>())
                .build();

        Actor savedActor = actorRepository.save(newActor);
        log.info(String.format(LogMessages.SAVED, EntityNames.ACTOR));

        return actorDtoConverter.convert(savedActor);
    }

    @Override
    public List<ActorDto> getActors(ActorFilteringOptions filteringOptions, OrderBy orderBy) {
        log.info(String.format(LogMessages.STARTED, "getActors"));
        List<Actor> actors = actorRepository.findAll();
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.ACTOR));

        if (filteringOptions.getMovieId() != null) {
            actors = actors
                    .stream()
                    .filter(actor -> actor.getMoviesPlayed()
                            .stream()
                            .map(Movie::getId)
                            .anyMatch(id -> filteringOptions.getMovieId().intValue() == id.intValue()))
                    .toList();
            log.info(String.format(LogMessages.FILTERED, "movieId"));
        }

        if (!StringUtils.isBlank(filteringOptions.getNationality())) {
            actors = actors.stream()
                    .filter(actor -> actor.getNationality().equals(filteringOptions.getNationality()))
                    .collect(Collectors.toList());
            log.info(String.format(LogMessages.FILTERED, "nationality"));
        }

        if (filteringOptions.getYear() != null) {
            actors = actors.stream()
                    .filter(actor ->  actor.getBirthYear().getYear() == filteringOptions.getYear())
                    .collect(Collectors.toList());
            log.info(String.format(LogMessages.FILTERED, "birthYear"));
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

            log.info(String.format(LogMessages.SORTED, "number of movies played"));
        }

        if (filteringOptions.getLimit() != null) {
            actors = actors.stream()
                    .limit(filteringOptions.getLimit())
                    .toList();
            log.info(String.format(String.format(LogMessages.LIMITED, filteringOptions.getLimit())));
        }

        return actors.stream()
                .map(actorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "actors", key = "#id", unless = "#result.moviesPlayed.size() < 2")
    @Override
    public ActorDto getActor(Integer id) {
        log.info(String.format(LogMessages.STARTED, "getActor"));
        Actor actorInDb = getActorById(id);
        log.info(String.format(LogMessages.FETCHED, EntityNames.ACTOR));
        return actorDtoConverter.convert(actorInDb);
    }

    @CacheEvict(value = "actors", allEntries = true)
    @Override
    public ActorDto updateActor(Integer id, UpdateActorRequest request) {
        log.info(String.format(LogMessages.STARTED, "updateActor"));
        Actor actorInDb = getActorById(id);
        log.info(String.format(LogMessages.FETCHED, EntityNames.ACTOR));

        actorInDb.setName(request.getName());
        actorInDb.setSurname(request.getSurname());
        actorInDb.setNationality(request.getNationality());
        actorInDb.setBirthYear(request.getBirthYear());
        actorInDb.setBiography(request.getBiography());
        log.info(LogMessages.FIELDS_SET);

        Actor savedActor = actorRepository.save(actorInDb);
        log.info(String.format(LogMessages.SAVED, EntityNames.ACTOR));

        return actorDtoConverter.convert(savedActor);
    }

    @CacheEvict(value = "actors", key = "#id")
    @Override
    public String deleteActor(Integer id) {
        log.info(String.format(LogMessages.STARTED, "deleteActor"));
        boolean actorExists = actorRepository.existsById(id);

        if (!actorExists) {
            throw new EntityNotFound(String.format(ResponseMessages.NOT_FOUND, EntityNames.ACTOR, id));
        }

        log.info(String.format(LogMessages.EXISTS, EntityNames.ACTOR));
        actorRepository.deleteById(id);
        log.info(String.format(LogMessages.DELETED, EntityNames.ACTOR));

        return String.format(ResponseMessages.SUCCESS, EntityNames.ACTOR, id, ActionNames.DELETED);
    }

    @Cacheable(value = "actors")
    @Override
    public List<ActorDto> getMostPopularActors() {
        log.info(String.format(LogMessages.STARTED, "getMostPopularActors"));
        List<Actor> actors = actorRepository.findAll();
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.ACTOR));
        int numberOfMovies = 2;

        return actors.stream()
                .filter(actor -> actor.getMoviesPlayed().size() >= numberOfMovies)
                .map(actorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActorDto> searchActors(String fullName) {
        log.info(LogMessages.STARTED, "searchActors");
        List<Actor> actors = actorRepository.findByFullName(fullName);
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.ACTOR));

        return actors.stream()
                .map(actorDtoConverter::convert)
                .toList();
    }

    @Override
    public CustomPage<ActorDto, Actor> getActors(Pageable pageable) {
        log.info(String.format(LogMessages.STARTED, "getActors"));
        Page<Actor> page = actorRepository.findAll(pageable);
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.ACTOR));

        List<ActorDto> actorDtoList = page.getContent()
                .stream()
                .map(actorDtoConverter::convert)
                .toList();

        return new CustomPage<>(page, actorDtoList);

    }

    private Actor getActorById(Integer id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format(ResponseMessages.NOT_FOUND, EntityNames.ACTOR, id)));
    }
}
