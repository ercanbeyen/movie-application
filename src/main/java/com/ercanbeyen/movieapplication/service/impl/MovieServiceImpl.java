package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.ActionNames;
import com.ercanbeyen.movieapplication.constant.message.EntityNames;
import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
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
import java.util.HashSet;
import java.util.List;
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
    public List<MovieDto> getMovies(MovieFilteringOptions filteringOptions, OrderBy orderBy) {
        log.info(String.format(LogMessages.STARTED, "getMovies"));
        List<Movie> movies = movieRepository.findAll();
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.MOVIE));

        if (filteringOptions.getGenres() != null && !filteringOptions.getGenres().isEmpty()) {
            movies = movies.stream()
                    .filter(movie -> filteringOptions.getGenres().contains(movie.getGenre()))
                    .toList();
            log.info(String.format(LogMessages.FILTERED, "genre"));
        }

        if (!StringUtils.isBlank(filteringOptions.getLanguage())) {
            movies = movies.stream()
                    .filter(movie -> movie.getLanguage().equals(filteringOptions.getLanguage()))
                    .collect(Collectors.toList());
            log.info(String.format(LogMessages.FILTERED, "language"));
        }

        if (filteringOptions.getYear() != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getReleaseYear().intValue() == filteringOptions.getYear().intValue())
                    .collect(Collectors.toList());
            log.info(String.format(LogMessages.FILTERED, "releaseYear"));
        }

        if (orderBy != null) {
            movies = movies.stream()
                    .sorted((movie1, movie2) -> {
                        Double rating1 = movie1.getRating();
                        Double rating2 = movie2.getRating();

                        if (orderBy == OrderBy.DESC) {
                            return Double.compare(rating2, rating1);
                        } else {
                            return Double.compare(rating1, rating2);
                        }
                    })
                    .toList();

            log.info(String.format(LogMessages.SORTED, "rating"));
        }

        if (filteringOptions.getLimit() != null) {
            movies = movies.stream()
                    .limit(filteringOptions.getLimit())
                    .toList();
            log.info(String.format(LogMessages.LIMITED, filteringOptions.getLimit()));
        }

        return movies.stream()
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
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

    @Override
    public CustomPage<MovieDto, Movie> getMovies(Pageable pageable) {
        log.info(String.format(LogMessages.STARTED, "getMovies"));

        Page<Movie> page = movieRepository.findAll(pageable);
        List<MovieDto> movieDtoList = page.getContent().stream()
                .map(movieDtoConverter::convert)
                .toList();

        return new CustomPage<>(page, movieDtoList);
    }
}
