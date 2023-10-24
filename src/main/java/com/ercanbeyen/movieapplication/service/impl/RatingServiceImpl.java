package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.RatingDto;
import com.ercanbeyen.movieapplication.dto.converter.RatingDtoConverter;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.Rating;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.RatingRepository;
import com.ercanbeyen.movieapplication.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public PageDto<Rating, RatingDto> getRatings(Pageable pageable) {
        Page<Rating> ratingPage = ratingRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.MOVIE);

        List<RatingDto> ratingDtoList = ratingPage.stream()
                .map(ratingDtoConverter::convert)
                .toList();

        return new PageDto<>(ratingPage, ratingDtoList);
    }

    @Override
    public RatingDto getRating(Integer id) {
        Rating ratingInDb = ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.RATING)));
        return ratingDtoConverter.convert(ratingInDb);
    }

    @Override
    public RatingDto updatedRating(Rating rating, Double rate) {
        rating.setRate(rate);
        Rating savedRating = ratingRepository.save(rating);
        log.info(LogMessages.SAVED, ResourceNames.RATING);
        return ratingDtoConverter.convert(savedRating);
    }
}
