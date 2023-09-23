package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByTitleStartingWith(String title);
    boolean existsByImdbId(String imdbId);
    Optional<Movie> findByImdbId(String imdbId);
}
