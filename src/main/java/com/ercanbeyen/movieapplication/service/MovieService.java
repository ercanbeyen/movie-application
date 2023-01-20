package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;

import java.util.List;

public interface MovieService {
    MovieDto createMovie(CreateMovieRequest request);
    MovieDto updateMovieRequest(Integer id, UpdateMovieRequest request);
    MovieDto getMovie(Integer id);
    List<MovieDto> getMovies();
    String deleteMovie(Integer id);
}
