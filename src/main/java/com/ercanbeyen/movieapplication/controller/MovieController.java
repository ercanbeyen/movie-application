package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.constant.defaults.DefaultValues;
import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.service.MovieService;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
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
    public ResponseEntity<?> createMovie(@RequestBody @Valid CreateMovieRequest request) {
        MovieDto movieDto = movieService.createMovie(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, movieDto);
    }

    @GetMapping({"", "/filter"})
    public ResponseEntity<?> filterMovies(MovieFilteringOptions movieFilteringOptions, @RequestParam(required = false) OrderBy orderBy, @RequestParam(required = false, defaultValue = DefaultValues.DEFAULT_LIMIT_VALUE) String limit, Pageable pageable) {
        PageDto<Movie, MovieDto> movieDtoList = movieService.filterMovies(movieFilteringOptions, orderBy, limit, pageable);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovie(@PathVariable Integer id) {
        MovieDto movieDto = movieService.getMovie(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Integer id, @RequestBody @Valid UpdateMovieRequest request) {
        MovieDto movieDto = movieService.updateMovie(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Integer id) {
        String message = movieService.deleteMovie(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestMovies() {
        List<MovieDto> movieDtoList = movieService.getLatestMovies();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtoList);

    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMovies(@RequestParam String title) {
        List<MovieDto> movieDtoList = movieService.searchMovies(title);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtoList);
    }

    @GetMapping("/find")
    public ResponseEntity<?> getMovie(@RequestParam(name = "imdb") String imdbId) {
        MovieDto movieDto = movieService.getMovie(imdbId);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDto);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        Statistics<String, String> statistics = movieService.calculateStatistics();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, statistics);
    }

}
