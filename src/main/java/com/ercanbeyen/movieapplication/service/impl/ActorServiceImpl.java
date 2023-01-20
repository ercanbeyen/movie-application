package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.converter.ActorDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.repository.ActorRepository;
import com.ercanbeyen.movieapplication.service.ActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final ActorDtoConverter actorDtoConverter;
    @Override
    public ActorDto createActor(CreateActorRequest request) {
        Actor newActor = Actor.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .moviesPlayed(new HashSet<>())
                .build();

        return actorDtoConverter.convert(actorRepository.save(newActor));
    }

    @Override
    public List<ActorDto> getActors() {
        List<Actor> actors = actorRepository.findAll();
        return actors.stream()
                .map(actorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public ActorDto getActor(Integer id) {
        Actor actorInDb = actorRepository.findById(id)
                .orElseThrow();
        return actorDtoConverter.convert(actorInDb);
    }

    @Override
    public ActorDto updateActor(Integer id, UpdateActorRequest request) {
        Actor actorInDb = actorRepository.findById(id)
                .orElseThrow();

        actorInDb.toBuilder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .build();

        return actorDtoConverter.convert(actorRepository.save(actorInDb));
    }

    @Override
    public String deleteActor(Integer id) {
        actorRepository.deleteById(id);
        return "Actor " + id + " is successfully deleted";
    }
}
