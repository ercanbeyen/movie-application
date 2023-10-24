package com.ercanbeyen.movieapplication.dto.converter;

import com.ercanbeyen.movieapplication.dto.RatingDto;
import com.ercanbeyen.movieapplication.entity.Rating;
import org.springframework.stereotype.Component;

@Component
public class RatingDtoConverter {
    public RatingDto convert(Rating rating) {
        return new RatingDto(
                rating.getId(),
                rating.getRate(),
                rating.getAudience().getId(),
                rating.getMovie().getId()
        );
    }
}
