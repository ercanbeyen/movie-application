package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.RatingDto;
import com.ercanbeyen.movieapplication.entity.Rating;
import com.ercanbeyen.movieapplication.service.RatingService;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<?> getMovies(Pageable pageable) {
        PageDto<Rating, RatingDto> ratingPageDto = ratingService.getRatings(pageable);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, ratingPageDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovie(@PathVariable Integer id) {
        RatingDto ratingDto = ratingService.getRating(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, ratingDto);
    }
}
