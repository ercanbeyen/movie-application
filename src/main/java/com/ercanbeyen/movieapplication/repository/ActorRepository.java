package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {
    @Query("Select actor " +
            "FROM Actor actor " +
            "WHERE CONCAT(actor.name, '_', actor.surname) LIKE CONCAT('%', :fullName, '%')")
    List<Actor> findByFullName(String fullName);
}
