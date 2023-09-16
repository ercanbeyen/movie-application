package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByTitleStartingWith(String title);
    boolean existsByImdbId(String imdbId);
    Optional<Movie> findByImdbId(String imdbId);
}
