package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.*;
import com.ercanbeyen.movieapplication.constant.names.ParameterNames;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.dto.converter.ActorDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.ActorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.ActorRepository;
import com.ercanbeyen.movieapplication.service.ActorService;
import com.ercanbeyen.movieapplication.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final ActorDtoConverter actorDtoConverter;

    @Override
    public ActorDto createActor(CreateActorRequest request) {
        log.info(LogMessages.STARTED, "createActor");

        Actor newActor = Actor.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .moviesPlayed(new HashSet<>())
                .build();

        Actor savedActor = actorRepository.save(newActor);
        log.info(LogMessages.SAVED, ResourceNames.ACTOR);

        return actorDtoConverter.convert(savedActor);
    }

    @Override
    public PageDto<Actor, ActorDto> filterActors(ActorFilteringOptions filteringOptions, OrderBy orderBy, String limit, Pageable pageable) {
        log.info(LogMessages.STARTED, "filterActors");
        Page<Actor> actorPage = actorRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.ACTOR);

        Predicate<Actor> actorPredicate = (actor) -> ((filteringOptions.movieId() == null || filteringOptions.movieId().intValue() == filteringOptions.movieId().intValue())) && (StringUtils.isBlank(filteringOptions.nationality()) || actor.getNationality().equals(filteringOptions.nationality()))
                && (filteringOptions.birthYear() == null || actor.getBirthYear().getYear() == filteringOptions.birthYear());

        long maximumSize = Long.parseLong(limit);

        if (orderBy == null) {
            log.info(LogMessages.REQUEST_PARAMETER_NULL, ParameterNames.ORDER_BY);

            List<ActorDto> actorDtoList = actorPage.stream()
                    .filter(actorPredicate)
                    .limit(maximumSize)
                    .map(actorDtoConverter::convert)
                    .toList();

            return new PageDto<>(actorPage, actorDtoList);
        }

        Comparator<Actor> actorComparator = Comparator.comparing(actor -> actor.getMoviesPlayed().size());
        log.info(LogMessages.ORDER_BY_VALUE, orderBy.getOrderByInfo());

        if (orderBy == OrderBy.DESC) {
            actorComparator = actorComparator.reversed();
        }

        List<ActorDto> actorDtoList = actorPage.stream()
                .filter(actorPredicate)
                .sorted(actorComparator)
                .limit(maximumSize)
                .map(actorDtoConverter::convert)
                .toList();

        return new PageDto<>(actorPage, actorDtoList);
    }

    @Cacheable(value = "actors", key = "#id", unless = "#result.moviesPlayed.size() < 2")
    @Override
    public ActorDto getActor(Integer id) {
        log.info(LogMessages.STARTED, "getActor");
        Actor actorInDb = getActorById(id);
        log.info(LogMessages.FETCHED, ResourceNames.ACTOR);
        return actorDtoConverter.convert(actorInDb);
    }

    @CacheEvict(value = "actors", allEntries = true)
    @Override
    public ActorDto updateActor(Integer id, UpdateActorRequest request) {
        log.info(LogMessages.STARTED, "updateActor");
        Actor actorInDb = getActorById(id);
        log.info(LogMessages.FETCHED, ResourceNames.ACTOR);

        actorInDb.setName(request.getName());
        actorInDb.setSurname(request.getSurname());
        actorInDb.setNationality(request.getNationality());
        actorInDb.setBirthYear(request.getBirthYear());
        actorInDb.setBiography(request.getBiography());
        log.info(LogMessages.FIELDS_SET);

        Actor savedActor = actorRepository.save(actorInDb);
        log.info(LogMessages.SAVED, ResourceNames.ACTOR);

        return actorDtoConverter.convert(savedActor);
    }

    @CacheEvict(value = "actors", key = "#id")
    @Override
    public String deleteActor(Integer id) {
        log.info(LogMessages.STARTED, "deleteActor");
        boolean actorExists = actorRepository.existsById(id);

        if (!actorExists) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ACTOR, id));
        }

        log.info(LogMessages.EXISTS, ResourceNames.ACTOR);
        actorRepository.deleteById(id);
        log.info(LogMessages.DELETED, ResourceNames.ACTOR);

        return String.format(ResponseMessages.SUCCESS, ResourceNames.ACTOR, id, ActionMessages.DELETED);
    }

    @Cacheable(value = "actors")
    @Override
    public List<ActorDto> getMostPopularActors() {
        log.info(LogMessages.STARTED, "getMostPopularActors");
        List<Actor> actors = actorRepository.findAll();
        log.info(LogMessages.FETCHED_ALL, ResourceNames.ACTOR);
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
        log.info(LogMessages.FETCHED_ALL, ResourceNames.ACTOR);

        return actors.stream()
                .map(actorDtoConverter::convert)
                .toList();
    }


    @Override
    public Actor findActorById(Integer id) {
        log.info(LogMessages.STARTED, "findActorById");
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ACTOR, id)));
    }

    @Override
    public Statistics<String, String> calculateStatistics() {
        log.info(LogMessages.STARTED, LogMessages.CALCULATE_STATISTICS);

        Map<String, String> statisticsMap = new HashMap<>();
        List<Actor> actorList = actorRepository.findAll();

        DoubleSummaryStatistics summaryStatistics = actorList.stream()
                .mapToDouble(actor -> actor.getMoviesPlayed().size())
                .summaryStatistics();

        statisticsMap.put("mostPlayedCount", String.valueOf(summaryStatistics.getMax()));
        statisticsMap.put("leastPlayedCount", String.valueOf(summaryStatistics.getMin()));
        statisticsMap.put("playedMovieSum", String.valueOf(summaryStatistics.getSum()));
        statisticsMap.put("playedMovieAverage", String.valueOf(summaryStatistics.getAverage()));
        statisticsMap.put("playerCount", String.valueOf(summaryStatistics.getCount()));

        return new Statistics<>(ResourceNames.ACTOR, statisticsMap);
    }

    private Actor getActorById(Integer id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ACTOR, id)));
    }
}
