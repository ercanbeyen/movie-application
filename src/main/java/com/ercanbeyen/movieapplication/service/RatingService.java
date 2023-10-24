package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.RatingDto;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.Rating;

public interface RatingService {
    RatingDto createRating(Audience audience, Movie movie, Double rate);
    RatingDto updatedRating(Rating rating, Double rate);
}
