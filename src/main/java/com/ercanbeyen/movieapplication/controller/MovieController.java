package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.dto.response.ResponseHandler;
import com.ercanbeyen.movieapplication.entity.enums.Genre;
import com.ercanbeyen.movieapplication.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Object> createMovie(@Valid @RequestBody CreateMovieRequest request) {
        MovieDto movieDto = movieService.createMovie(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, movieDto);
    }

    @GetMapping
    public ResponseEntity<Object> getMovies(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) Integer year) {
        List<MovieDto> movieDtos = movieService.getMovies(language, genre, year);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getMovie(@PathVariable("id") Integer id) {
        MovieDto movieDto = movieService.getMovie(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMovie(@PathVariable("id") Integer id, @Valid @RequestBody UpdateMovieRequest request) {
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
        List<MovieDto> movieDtos = movieService.getLatestMovies();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchMovies(@RequestParam(required = false) String title) {
        List<MovieDto> movieDtos = movieService.searchMovies(title);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, movieDtos);
    }
}
