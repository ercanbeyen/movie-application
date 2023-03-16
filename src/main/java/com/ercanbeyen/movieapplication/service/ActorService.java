package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.util.CustomPage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActorService {
    ActorDto createActor(CreateActorRequest request);
    List<ActorDto> getActors(String nationality, Integer year, Integer movieId, Boolean sort, Boolean descending, Integer limit);
    ActorDto getActor(Integer id);
    ActorDto updateActor(Integer id, UpdateActorRequest request);
    String deleteActor(Integer id);
    List<ActorDto> getMostPopularActors();
    List<ActorDto> searchActors(String fullName);
    CustomPage<ActorDto, Actor> getActors(Pageable pageable);
}
