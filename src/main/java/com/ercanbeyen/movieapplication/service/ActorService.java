package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;

import java.util.List;

public interface ActorService {
    ActorDto createActor(CreateActorRequest request);
    List<ActorDto> getActors();
    ActorDto getActor(Integer id);
    ActorDto updateActor(Integer id, UpdateActorRequest request);
    String deleteActor(Integer id);
}
