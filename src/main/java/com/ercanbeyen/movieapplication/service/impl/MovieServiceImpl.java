package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.*;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.exception.EntityNotFound;
import com.ercanbeyen.movieapplication.repository.MovieRepository;
import com.ercanbeyen.movieapplication.service.DirectorService;
import com.ercanbeyen.movieapplication.service.MovieService;
import com.ercanbeyen.movieapplication.util.CustomPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieDtoConverter movieDtoConverter;
    private final DirectorService directorService;

    @CachePut(value = "movies", key = "#result.id")
    @Override
    public MovieDto createMovie(CreateMovieRequest request) {
        log.info(String.format(LogMessages.STARTED, "createMovie"));
        Director director = directorService.getDirectorById(request.getDirectorId());
        log.info("Director is found");

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
        log.info(String.format(LogMessages.SAVED, EntityNames.MOVIE));

        return movieDtoConverter.convert(createdMovie);
    }

    @CacheEvict(value = "movies", allEntries = true)
    @Override
    public CustomPage<Movie, MovieDto> filterMovies(MovieFilteringOptions filteringOptions, OrderBy orderBy, Pageable pageable) {
        log.info(String.format(LogMessages.STARTED, "filterMovies"));
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.MOVIE));

        Predicate<Movie> moviePredicate = (movie) -> (
                (filteringOptions.getGenres() == null || filteringOptions.getGenres().isEmpty() || filteringOptions.getGenres().contains(movie.getGenre())) &&
                (StringUtils.isBlank(filteringOptions.getLanguage()) || movie.getLanguage().equals(filteringOptions.getLanguage())) &&
                (filteringOptions.getReleaseYear() == null || movie.getReleaseYear().intValue() == filteringOptions.getReleaseYear().intValue()));

        if (filteringOptions.getLimit() == null) {
            log.info(String.format(LogMessages.PARAMETER_NULL, ParameterNames.LIMIT));
            filteringOptions.setLimit(movieRepository.count());
        }

        if (orderBy == null) {
            log.info(String.format(LogMessages.PARAMETER_NULL, ParameterNames.ORDER_BY));

            List<MovieDto> movieDtoList = moviePage.stream()
                    .filter(moviePredicate)
                    .limit(filteringOptions.getLimit())
                    .map(movieDtoConverter::convert)
                    .toList();

            return new CustomPage<>(moviePage, movieDtoList);
        }

        Comparator<Movie> movieComparator = Comparator.comparing(Movie::getRating);
        log.info(String.format(LogMessages.ORDER_BY_VALUE, orderBy.name()));

        if (orderBy == OrderBy.DESC) {
            movieComparator = movieComparator.reversed();
        }

        List<MovieDto> movieDtoList = moviePage.stream()
                .filter(moviePredicate)
                .sorted(movieComparator)
                .limit(filteringOptions.getLimit())
                .map(movieDtoConverter::convert)
                .toList();

        return new CustomPage<>(moviePage, movieDtoList);
    }

    @Cacheable(value = "movies", key = "#id", unless = "#result.releaseYear < 2020")
    @Override
    public MovieDto getMovie(Integer id) {
        log.info(String.format(LogMessages.STARTED, "getMovie"));
        Movie movieInDb = getMovieById(id);
        log.info(String.format(LogMessages.FETCHED, EntityNames.MOVIE));
        return movieDtoConverter.convert(movieInDb);
    }

    @CacheEvict(value = "movies", allEntries = true)
    @Override
    public MovieDto updateMovie(Integer id, UpdateMovieRequest request) {
        log.info(String.format(LogMessages.STARTED, "updateMovie"));
        Movie movieInDb = getMovieById(id);
        log.info(String.format(LogMessages.FETCHED, EntityNames.MOVIE));

        movieInDb.setTitle(request.getTitle());
        movieInDb.setGenre(request.getGenre());
        movieInDb.setLanguage(request.getLanguage());
        movieInDb.setReleaseYear(request.getReleaseYear());
        movieInDb.setRating(request.getRating());
        movieInDb.setSummary(request.getSummary());
        log.info(LogMessages.FIELDS_SET);

        Movie savedMovie = movieRepository.save(movieInDb);
        log.info(String.format(LogMessages.SAVED, EntityNames.MOVIE));

        return movieDtoConverter.convert(savedMovie);
    }

    @CacheEvict(value = "movies", key = "#id")
    @Override
    public String deleteMovie(Integer id) {
        log.info(String.format(LogMessages.STARTED, "deleteMovie"));

        boolean movieExists = movieRepository.existsById(id);

        if (!movieExists) {
            throw new EntityNotFound(String.format(ResponseMessages.NOT_FOUND, EntityNames.MOVIE, id));
        }

        log.info(String.format(LogMessages.EXISTS, EntityNames.MOVIE));
        movieRepository.deleteById(id);
        log.info(String.format(LogMessages.DELETED, EntityNames.MOVIE));

        return String.format(ResponseMessages.SUCCESS, EntityNames.MOVIE, id, ActionNames.DELETED);
    }

    @Cacheable(value = "movies")
    @Override
    public List<MovieDto> getLatestMovies() {
        log.info(String.format(LogMessages.STARTED, "getLatestMovies"));

        List<Movie> movies = movieRepository.findAll();
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.MOVIE));
        int year = 2020;

        return movies.stream()
                .filter(movie -> movie.getReleaseYear() >= year)
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> searchMovies(String title) {
        log.info(String.format(LogMessages.STARTED, "searchMovies"));
        boolean isTitleFilled = StringUtils.isNotBlank(title);
        List<Movie> movies = new ArrayList<>();

        if (isTitleFilled) {
            movies = movieRepository.findByTitleStartingWith(title);
            log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.MOVIE));
        }

        return movies.stream()
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Movie getMovieById(Integer id) {
        log.info(String.format(LogMessages.STARTED, "getMovieById"));
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format(ResponseMessages.NOT_FOUND, EntityNames.MOVIE, id)));
    }

}
