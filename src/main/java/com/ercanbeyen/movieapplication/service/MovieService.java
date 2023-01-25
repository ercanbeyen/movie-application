package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.enums.Genre;

import java.util.List;

public interface MovieService {
    MovieDto createMovie(CreateMovieRequest request);
    List<MovieDto> getMovies(String language, Genre genre, Integer year);
    MovieDto getMovie(Integer id);
    MovieDto updateMovie(Integer id, UpdateMovieRequest request);
    String deleteMovie(Integer id);
    //List<MovieDto> getLatestMovies(Integer year);
    List<MovieDto> getLatestMovies();
    Movie getMovieById(Integer id);

}
