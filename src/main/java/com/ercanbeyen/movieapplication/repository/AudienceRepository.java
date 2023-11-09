package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.dto.AudienceDto;
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
    @Query("""
           SELECT new com.ercanbeyen.movieapplication.dto.AudienceDto(
                audience.id, audience.name, audience.surname, audience.nationality, audience.birthDate)
           FROM Audience audience
           WHERE audience.id = :id
           """)
    Optional<AudienceDto> findAudienceDtoById(@Param("id") Integer id);
    @Query("""
           SELECT new com.ercanbeyen.movieapplication.dto.AudienceDto(
                audience.id, audience.name, audience.surname, audience.nationality, audience.birthDate)
           FROM Audience audience
           WHERE audience.username = :username
           """)
    Optional<AudienceDto> findAudienceDtoByUsername(@Param("username") String username);
}
