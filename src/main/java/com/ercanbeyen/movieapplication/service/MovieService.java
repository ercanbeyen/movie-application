package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.OrderBy;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.util.CustomPage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {
    MovieDto createMovie(CreateMovieRequest request);
    List<MovieDto> getMovies(MovieFilteringOptions filteringOptions, OrderBy orderBy);
    MovieDto getMovie(Integer id);
    MovieDto updateMovie(Integer id, UpdateMovieRequest request);
    String deleteMovie(Integer id);
    List<MovieDto> getLatestMovies();
    List<MovieDto> searchMovies(String title);
    Movie getMovieById(Integer id);
    CustomPage<MovieDto, Movie> getMovies(Pageable pageable);
}
