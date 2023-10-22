package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.Rating;

public interface RatingService {
    String createRating(Audience audience, Movie movie, Double rate);
    String updatedRating(Rating rating, Double rate);
}
