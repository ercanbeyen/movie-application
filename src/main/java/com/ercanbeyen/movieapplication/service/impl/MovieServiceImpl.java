package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.repository.MovieRepository;
import com.ercanbeyen.movieapplication.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieDtoConverter movieDtoConverter;

    @Override
    public MovieDto createMovie(CreateMovieRequest request) {
        Movie newMovie = Movie.builder()
                .title(request.getTitle())
                .genre(request.getGenre())
                .rating(request.getRating())
                .releaseYear(request.getReleaseYear())
                .language(request.getLanguage())
                .summary(request.getSummary())
                .actors(new HashSet<>())
                .build();

        return movieDtoConverter.convert(movieRepository.save(newMovie));
    }

    @Override
    public List<MovieDto> getMovies() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public MovieDto getMovie(Integer id) {
        Movie movieInDb = movieRepository.findById(id)
                .orElseThrow();
        return movieDtoConverter.convert(movieInDb);
    }

    @Override
    public MovieDto updateMovie(Integer id, UpdateMovieRequest request) {
        Movie movieInDb = movieRepository.findById(id)
                .orElseThrow();

        movieInDb.toBuilder()
                .title(request.getTitle())
                .genre(request.getGenre())
                .language(request.getLanguage())
                .rating(request.getRating())
                .summary(request.getSummary())
                .build();

        return movieDtoConverter.convert(movieRepository.save(movieInDb));
    }

    @Override
    public String deleteMovie(Integer id) {
        movieRepository.deleteById(id);
        return "Movie " + id + " is successfully deleted";
    }
}
