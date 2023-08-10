package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.service.MovieService;
import com.ercanbeyen.movieapplication.util.CustomPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<Object> createMovie(@RequestBody @Valid CreateMovieRequest request) {
        MovieDto movieDto = movieService.createMovie(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, movieDto);
    }

    @GetMapping
    public ResponseEntity<Object> filterMovies(MovieFilteringOptions movieFilteringOptions, @RequestParam(required = false) OrderBy orderBy, Pageable pageable) {
        CustomPage<Movie, MovieDto> movieDtoList = movieService.filterMovies(movieFilteringOptions, orderBy, pageable);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMovie(@PathVariable("id") Integer id) {
        MovieDto movieDto = movieService.getMovie(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMovie(@PathVariable("id") Integer id, @RequestBody @Valid UpdateMovieRequest request) {
        MovieDto movieDto = movieService.updateMovie(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMovie(@PathVariable("id") Integer id) {
        String message = movieService.deleteMovie(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }

    @GetMapping("/latest")
    public ResponseEntity<Object> getLatestMovies() {
        List<MovieDto> movieDtoList = movieService.getLatestMovies();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtoList);

    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchMovies(@RequestParam(required = false) String title) {
        List<MovieDto> movieDtoList = movieService.searchMovies(title);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtoList);
    }

}
