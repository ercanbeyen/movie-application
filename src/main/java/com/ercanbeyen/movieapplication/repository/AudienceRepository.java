package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Audience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudienceRepository extends JpaRepository<Audience, Integer> {

}
