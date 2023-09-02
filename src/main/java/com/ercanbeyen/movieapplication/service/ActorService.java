package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.dto.option.filter.ActorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.dto.PageDto;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ActorService {
    ActorDto createActor(CreateActorRequest request);
    PageDto<Actor, ActorDto> filterActors(ActorFilteringOptions filteringOptions, OrderBy orderBy, String limit, Pageable pageable);
    ActorDto getActor(Integer id);
    ActorDto updateActor(Integer id, UpdateActorRequest request);
    String deleteActor(Integer id);
    List<ActorDto> getMostPopularActors();
    List<ActorDto> searchActors(String fullName);
    Actor findActorById(Integer id);

}
