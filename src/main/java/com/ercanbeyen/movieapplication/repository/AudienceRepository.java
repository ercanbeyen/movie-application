package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Audience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AudienceRepository extends JpaRepository<Audience, Integer> {
    Optional<Audience> findByUsername(String username);
}
