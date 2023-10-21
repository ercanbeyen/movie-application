package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.entity.Movie;

public interface RatingService {
    String createRating(Audience audience, Movie movie, Double rate);
}
