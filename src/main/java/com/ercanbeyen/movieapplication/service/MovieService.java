package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;

import java.util.List;

public interface MovieService {
    MovieDto createMovie(CreateMovieRequest request);
    List<MovieDto> getMovies();
    MovieDto getMovie(Integer id);
    MovieDto updateMovie(Integer id, UpdateMovieRequest request);
    String deleteMovie(Integer id);
}
