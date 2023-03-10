package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DirectorRepository extends JpaRepository<Director, Integer> {
    //List<Director> findByNameAndSurnameLike(String name, String surname);
    @Query("SELECT director " +
            "FROM Director director " +
            "WHERE CONCAT(director.name, '_', director.surname) LIKE CONCAT('%', :fullName, '%')")
    List<Director> findByFullName(String fullName);
}
