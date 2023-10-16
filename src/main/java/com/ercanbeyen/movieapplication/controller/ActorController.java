package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.annotation.LogExecutionTime;
import com.ercanbeyen.movieapplication.constant.defaults.DefaultValues;
import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.option.filter.ActorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.service.ActorService;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/actors")
@RequiredArgsConstructor
public class ActorController {
    private final ActorService actorService;

    @PostMapping
    public ResponseEntity<?> createActor(@RequestBody @Valid CreateActorRequest request) throws JsonProcessingException {
        ActorDto createdActor = actorService.createActor(request);
        Map<String, ?> partialData = ResponseHandler.getAllSerializedData(createdActor);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, partialData);
    }

    @LogExecutionTime
    @GetMapping({"", "/filter"})
    public ResponseEntity<?> getActors(ActorFilteringOptions actorFilteringOptions, @RequestParam(required = false) OrderBy orderBy, @RequestParam(required = false, defaultValue = DefaultValues.DEFAULT_LIMIT_VALUE) String limit, Pageable pageable) throws JsonProcessingException {
        PageDto<Actor, ActorDto> actorDtoList = actorService.getActors(actorFilteringOptions, orderBy, limit, pageable);
        Map<String, ?> partialData = ResponseHandler.getSerializedPartialData(actorDtoList, "name", "surname");
        return ResponseHandler.generateResponse(HttpStatus.OK, null, partialData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getActor(@PathVariable Integer id, final String... fields) throws JsonProcessingException {
        ActorDto actorDto = actorService.getActor(id);
        Map<String, ?> partialData = ResponseHandler.getSerializedPartialData(actorDto, fields);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, partialData);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActor(@PathVariable Integer id, @RequestBody @Valid UpdateActorRequest request) throws JsonProcessingException {
        ActorDto updatedActor = actorService.updateActor(id, request);
        Map<String, ?> partialData = ResponseHandler.getAllSerializedData(updatedActor);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, partialData);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable Integer id) {
        String message = actorService.deleteActor(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getMostPopularActors() throws JsonProcessingException {
        List<ActorDto> actorDtoList = actorService.getMostPopularActors();
        Map<String, ?> partialData = ResponseHandler.getFilteredPartialData(actorDtoList, "moviesPlayed", "summary");
        return ResponseHandler.generateResponse(HttpStatus.OK, null, partialData);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchActors(@RequestParam String fullName) throws JsonProcessingException {
        List<ActorDto> actorDtoList = actorService.searchActors(fullName);
        Map<String, ?> partialData = ResponseHandler.getFilteredPartialData(actorDtoList, "moviesPlayed", "summary");
        return ResponseHandler.generateResponse(HttpStatus.OK, null, partialData);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> calculateStatistics() {
        Statistics<String, String> statistics = actorService.calculateStatistics();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, statistics);
    }

}
