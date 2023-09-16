package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.*;
import com.ercanbeyen.movieapplication.constant.names.ParameterNames;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.MovieRepository;
import com.ercanbeyen.movieapplication.service.ActorService;
import com.ercanbeyen.movieapplication.service.DirectorService;
import com.ercanbeyen.movieapplication.service.MovieService;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.util.StatisticsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieDtoConverter movieDtoConverter;
    private final DirectorService directorService;
    private final ActorService actorService;

    @CachePut(value = "movies", key = "#result.id")
    @Override
    public MovieDto createMovie(CreateMovieRequest request) {
        log.info(LogMessages.STARTED, "createMovie");
        Director director = null;

        if (request.getDirectorId() != null) {
            director = directorService.findDirectorById(request.getDirectorId());
            log.info(LogMessages.RESOURCE_FOUND);
        } else {
            log.warn(LogMessages.SEARCH_SKIPPED, ResourceNames.DIRECTOR);
        }

        Movie newMovie = Movie.builder()
                .title(request.getTitle())
                .genre(request.getGenre())
                .rating(request.getRating())
                .releaseYear(request.getReleaseYear())
                .language(request.getLanguage())
                .summary(request.getSummary())
                .director(director)
                .actors(new HashSet<>())
                .build();

        Movie createdMovie = movieRepository.save(newMovie);
        log.info(LogMessages.SAVED, ResourceNames.MOVIE);

        return movieDtoConverter.convert(createdMovie);
    }

    @CacheEvict(value = "movies", allEntries = true)
    @Override
    public PageDto<Movie, MovieDto> filterMovies(MovieFilteringOptions filteringOptions, OrderBy orderBy, String limit, Pageable pageable) {
        log.info(LogMessages.STARTED, "filterMovies");
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.MOVIE);

        Predicate<Movie> moviePredicate = (movie) -> (
                (filteringOptions.getGenres() == null || filteringOptions.getGenres().isEmpty() || filteringOptions.getGenres().contains(movie.getGenre())) &&
                (StringUtils.isBlank(filteringOptions.getLanguage()) || movie.getLanguage().equals(filteringOptions.getLanguage())) &&
                (filteringOptions.getReleaseYear() == null || movie.getReleaseYear().intValue() == filteringOptions.getReleaseYear().intValue()));

        long maximumSize = Long.parseLong(limit);

        if (orderBy == null) {
            log.info(LogMessages.REQUEST_PARAMETER_NULL, ParameterNames.ORDER_BY);

            List<MovieDto> movieDtoList = moviePage.stream()
                    .filter(moviePredicate)
                    .limit(maximumSize)
                    .map(movieDtoConverter::convert)
                    .toList();

            return new PageDto<>(moviePage, movieDtoList);
        }

        Comparator<Movie> movieComparator = Comparator.comparing(Movie::getRating);
        log.info(LogMessages.ORDER_BY_VALUE, orderBy.getOrderByInfo());

        if (orderBy == OrderBy.DESC) {
            movieComparator = movieComparator.reversed();
        }

        List<MovieDto> movieDtoList = moviePage.stream()
                .filter(moviePredicate)
                .sorted(movieComparator)
                .limit(maximumSize)
                .map(movieDtoConverter::convert)
                .toList();

        return new PageDto<>(moviePage, movieDtoList);
    }

    @Cacheable(value = "movies", key = "#id", unless = "#result.releaseYear < 2020")
    @Override
    public MovieDto getMovie(Integer id) {
        log.info(LogMessages.STARTED, "getMovie");
        Movie movieInDb = findMovieById(id);
        log.info(LogMessages.FETCHED, ResourceNames.MOVIE);
        return movieDtoConverter.convert(movieInDb);
    }

    @CacheEvict(value = "movies", allEntries = true)
    @Transactional
    @Override
    public MovieDto updateMovie(Integer id, UpdateMovieRequest request) {
        log.info(LogMessages.STARTED, "updateMovie");

        Movie movieInDb = findMovieById(id);
        log.info(LogMessages.FETCHED, ResourceNames.MOVIE);

        if (request.getDirectorId() != null) {
            Director director = directorService.findDirectorById(request.getDirectorId());
            movieInDb.setDirector(director);
            log.info(LogMessages.RESOURCE_FOUND);
        } else {
            log.warn(LogMessages.SEARCH_SKIPPED, ResourceNames.DIRECTOR);
        }

        movieInDb.getActors().clear();

        if (request.getActorIds() != null) {
            Set<Actor> actorSet = new HashSet<>();
            for (Integer actorId : request.getActorIds()) {
                Actor actorInDb = actorService.findActorById(actorId);
                actorInDb.getMoviesPlayed().add(movieInDb);
                actorSet.add(actorInDb);
                log.info(LogMessages.RESOURCE_FOUND);
            }

            movieInDb.setActors(actorSet);
        } else {
            log.warn(LogMessages.SEARCH_SKIPPED, ResourceNames.ACTOR);
        }

        movieInDb.setTitle(request.getTitle());
        movieInDb.setGenre(request.getGenre());
        movieInDb.setLanguage(request.getLanguage());
        movieInDb.setReleaseYear(request.getReleaseYear());
        movieInDb.setRating(request.getRating());
        movieInDb.setSummary(request.getSummary());
        log.info(LogMessages.FIELDS_SET);

        Movie savedMovie = movieRepository.save(movieInDb);
        log.info(LogMessages.SAVED, ResourceNames.MOVIE);

        return movieDtoConverter.convert(savedMovie);
    }

    @CacheEvict(value = "movies", key = "#id")
    @Override
    public String deleteMovie(Integer id) {
        log.info(LogMessages.STARTED, "deleteMovie");

        boolean movieExists = movieRepository.existsById(id);

        if (!movieExists) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE, id));
        }

        log.info(LogMessages.EXISTS, ResourceNames.MOVIE);
        movieRepository.deleteById(id);
        log.info(LogMessages.DELETED, ResourceNames.MOVIE);

        return String.format(ResponseMessages.SUCCESS, ResourceNames.MOVIE, id, ActionMessages.DELETED);
    }

    @Cacheable(value = "movies")
    @Override
    public List<MovieDto> getLatestMovies() {
        log.info(LogMessages.STARTED, "getLatestMovies");

        List<Movie> movies = movieRepository.findAll();
        log.info(LogMessages.FETCHED_ALL, ResourceNames.MOVIE);
        int year = 2020;

        return movies.stream()
                .filter(movie -> movie.getReleaseYear() >= year)
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> searchMovies(String title) {
        log.info(LogMessages.STARTED, "searchMovies");

        List<Movie> movies = movieRepository.findByTitleStartingWith(title);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.MOVIE);


        return movies.stream()
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Movie findMovieById(Integer id) {
        log.info(LogMessages.STARTED, "findMovieById");
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE, id)));
    }

    @Override
    public Statistics<String, String> calculateStatistics() {
        log.info(LogMessages.STARTED, LogMessages.CALCULATE_STATISTICS);
        Statistics<String, String> statistics = new Statistics<>();

        statistics.setTopic(ResourceNames.MOVIE);

        Map<String, String> statisticsMap = new HashMap<>();
        List<Movie> movieList = movieRepository.findAll();

        Comparator<Movie> movieComparator = Comparator.comparing(Movie::getRating);

        String titleOfMostRatedMovie = movieList.stream()
                .max(movieComparator)
                .map(Movie::getTitle)
                .orElse(StatisticsMessages.NOT_EXISTS);

        statisticsMap.put("mostRatedMovie", titleOfMostRatedMovie);

        String titleOfLeastRatedMovie = movieList.stream()
                .min(movieComparator)
                .map(Movie::getTitle)
                .orElse(StatisticsMessages.NOT_EXISTS);

        statisticsMap.put("leastRatedMovie", titleOfLeastRatedMovie);

        List<String> languageList = movieList.stream()
                .map(Movie::getLanguage)
                .toList();

        String mostPopularLanguage = StatisticsUtil.calculateMostOccurred(languageList);
        mostPopularLanguage = StatisticsUtil.valueAssignmentToStringItem(mostPopularLanguage);
        statisticsMap.put("mostPopularLanguage", mostPopularLanguage);

        String leastPopularLanguage = StatisticsUtil.calculateLeastOccurred(languageList);
        leastPopularLanguage = StatisticsUtil.valueAssignmentToStringItem(leastPopularLanguage);
        statisticsMap.put("leastPopularLanguage", leastPopularLanguage);

        statistics.setResult(statisticsMap);
        return statistics;
    }

}
