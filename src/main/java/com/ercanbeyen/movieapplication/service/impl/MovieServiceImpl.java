package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.enums.Genre;
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
        Director director = directorService.getDirectorById(request.getDirectorId());

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

        return movieDtoConverter.convert(createdMovie);
    }

    @CacheEvict(value = "movies", allEntries = true)
    @Override
    public List<MovieDto> getMovies(String language, Genre genre, Integer year,  Boolean sort, Boolean descending, Integer limit) {
        log.info("Fetch movies from database");
        List<Movie> movies = movieRepository.findAll();

        if (!StringUtils.isBlank(language)) {
            movies = movies.stream()
                    .filter(movie -> movie.getLanguage().equals(language))
                    .collect(Collectors.toList());
            log.info("Movies are filtered by language");
        }

        if (genre != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getGenre() == genre)
                    .collect(Collectors.toList());
            log.info("Movies are filtered by genre");
        }

        if (year != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getReleaseYear() == year)
                    .collect(Collectors.toList());
            log.info("Movies are filtered by release year");
        }

        if (sort != null && sort) {
            movies = movies.stream()
                    .sorted((movie1, movie2) -> {
                        Double rating1 = movie1.getRating();
                        Double rating2 = movie2.getRating();

                        if (descending != null && descending) {
                            return Double.compare(rating2, rating1);
                        } else {
                            return Double.compare(rating1, rating2);
                        }
                    })
                    .toList();

            log.info("Movies are sorted by rating");

            if (limit != null) {
                movies = movies.stream()
                        .limit(limit)
                        .toList();
            }

            log.info("Top {} movies are selected", limit);
        }

        return movies.stream()
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "movies", key = "#id", unless = "#result.releaseYear < 2020")
    @Override
    public MovieDto getMovie(Integer id) {
        log.info("Fetch movie from database");
        Movie movieInDb = getMovieById(id);
        return movieDtoConverter.convert(movieInDb);
    }

    @CacheEvict(value = "movies", allEntries = true)
    @Override
    public MovieDto updateMovie(Integer id, UpdateMovieRequest request) {
        log.info("Update movie from database");
        Movie movieInDb = getMovieById(id);

        movieInDb.setTitle(request.getTitle());
        movieInDb.setGenre(request.getGenre());
        movieInDb.setLanguage(request.getLanguage());
        movieInDb.setReleaseYear(request.getReleaseYear());
        movieInDb.setRating(request.getRating());
        movieInDb.setSummary(request.getSummary());

        return movieDtoConverter.convert(movieRepository.save(movieInDb));
    }

    @CacheEvict(value = "movies", key = "#id")
    @Override
    public String deleteMovie(Integer id) {
        log.info("Delete movie from database");
        movieRepository.deleteById(id);
        return "Movie " + id + " is successfully deleted";
    }

    @Cacheable(value = "movies")
    @Override
    public List<MovieDto> getLatestMovies() {
        log.info("Fetch latest movies from database");
        List<Movie> movies = movieRepository.findAll();
        int year = 2020;

        return movies.stream()
                .filter(movie -> movie.getReleaseYear() >= year)
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> searchMovies(String title) {
        boolean isTitleFilled = StringUtils.isNotBlank(title);
        List<Movie> movies = new ArrayList<>();

        if (isTitleFilled) {
            movies = movieRepository.findByTitleStartingWith(title);
        }

        return movies.stream()
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Movie getMovieById(Integer id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Movie " + id + " is not found"));
    }

    @Override
    public CustomPage<MovieDto, Movie> getMovies(Pageable pageable) {
        Page<Movie> page = movieRepository.findAll(pageable);
        List<MovieDto> movieDtos = page.getContent().stream()
                .map(movieDtoConverter::convert)
                .toList();

        return new CustomPage<>(page, movieDtos);
    }
}
