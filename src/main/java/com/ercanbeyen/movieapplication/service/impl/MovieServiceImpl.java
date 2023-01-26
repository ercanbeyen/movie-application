package com.ercanbeyen.movieapplication.service.impl;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    public List<MovieDto> getMovies(String language, Genre genre, Integer year) {
        log.info("Fetch movies from database");
        List<Movie> movies = movieRepository.findAll();

        if (!StringUtils.isBlank(language)) {
            movies = movies.stream()
                    .filter(movie -> movie.getLanguage().equals(language))
                    .collect(Collectors.toList());
        }

        if (genre != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getGenre() == genre)
                    .collect(Collectors.toList());
        }

        if (year != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getReleaseYear() == year)
                    .collect(Collectors.toList());
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

    @CachePut(value = "movies", key = "#id", unless = "#result.releaseYear < 2020")
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

    //@Cacheable(value = "movies", unless = "#result.releaseYear < 2020")
    @Cacheable(value = "movies")
    //@Cacheable(value = "movies", unless = "#result.getReleaseYear() < 2020")
    @Override
    public List<MovieDto> getLatestMovies() {
        log.info("Fetch latest movies from database");
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .filter(movie -> movie.getReleaseYear() >= 2020)
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
}
