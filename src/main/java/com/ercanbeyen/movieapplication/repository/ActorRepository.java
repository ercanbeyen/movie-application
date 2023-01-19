package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Integer> {

}
