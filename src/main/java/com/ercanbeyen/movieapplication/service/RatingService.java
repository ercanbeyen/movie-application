package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.RatingDto;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.Rating;
import org.springframework.data.domain.Pageable;

public interface RatingService {
    RatingDto createRating(Audience audience, Movie movie, Double rate);
    PageDto<Rating, RatingDto> getRatings(Pageable pageable);
    RatingDto getRating(Integer movieId, Integer audienceId);
    RatingDto updatedRating(Rating rating, Double rate);
}
