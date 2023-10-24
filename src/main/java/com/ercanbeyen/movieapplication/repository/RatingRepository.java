package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    Optional<Rating> findByMovieIdAndAudienceId(Integer movieId, Integer audienceId);
}
