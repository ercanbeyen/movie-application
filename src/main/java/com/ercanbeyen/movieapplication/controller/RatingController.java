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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<?> getRatings(Pageable pageable) {
        PageDto<Rating, RatingDto> ratingPageDto = ratingService.getRatings(pageable);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, ratingPageDto);
    }

    @GetMapping("/find")
    public ResponseEntity<?> getRating(@RequestParam(name = "movie") Integer movieId, @RequestParam(name = "audience") Integer audienceId) {
        RatingDto ratingDto = ratingService.getRating(movieId, audienceId);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, ratingDto);
    }
}
