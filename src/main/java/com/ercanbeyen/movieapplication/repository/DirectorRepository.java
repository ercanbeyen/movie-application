package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorRepository extends JpaRepository<Director, Integer> {

}
