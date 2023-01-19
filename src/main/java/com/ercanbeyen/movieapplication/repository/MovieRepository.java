package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

}
