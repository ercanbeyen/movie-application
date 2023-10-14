package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {
    MovieDto createMovie(CreateMovieRequest request);
    PageDto<Movie, MovieDto> getMovies(MovieFilteringOptions filteringOptions, OrderBy orderBy, String limit, Pageable pageable);
    MovieDto getMovie(Integer id);
    MovieDto updateMovie(Integer id, UpdateMovieRequest request);
    String deleteMovie(Integer id);
    List<MovieDto> getLatestMovies();
    List<MovieDto> searchMovies(String title);
    MovieDto getMovie(String imdbId);
    Movie findMovieById(Integer id);
    Statistics<String, String> calculateStatistics();
}
