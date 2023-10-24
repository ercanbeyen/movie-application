package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.RatingDto;
import com.ercanbeyen.movieapplication.dto.converter.RatingDtoConverter;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.Rating;
import com.ercanbeyen.movieapplication.repository.RatingRepository;
import com.ercanbeyen.movieapplication.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RatingDtoConverter ratingDtoConverter;
    @Override
    public RatingDto createRating(Audience audience, Movie movie, Double rate) {
        Rating newRating = new Rating();
        newRating.setAudience(audience);
        newRating.setMovie(movie);
        newRating.setRate(rate);

        Rating savedRating = ratingRepository.save(newRating);
        log.info(LogMessages.SAVED, ResourceNames.RATING);

        return ratingDtoConverter.convert(savedRating);
    }

    @Override
    public RatingDto updatedRating(Rating rating, Double rate) {
        rating.setRate(rate);
        Rating savedRating = ratingRepository.save(rating);
        log.info(LogMessages.SAVED, ResourceNames.RATING);
        return ratingDtoConverter.convert(savedRating);
    }
}
