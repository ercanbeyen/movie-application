package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.entity.Audience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public interface AudienceRepository extends JpaRepository<Audience, Integer> {
    @Query("""
           SELECT audience
           FROM Audience audience
           WHERE audience.username = :username
           """
    )
    Optional<Audience> findAudience(@Param("username") String username);

    CompletableFuture<Audience> findByUsername(String username);
}
