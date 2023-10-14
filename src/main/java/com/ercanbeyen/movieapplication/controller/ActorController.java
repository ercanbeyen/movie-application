package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.constant.annotation.LogExecutionTime;
import com.ercanbeyen.movieapplication.constant.defaults.DefaultValues;
import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.dto.option.filter.ActorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.service.ActorService;
import com.ercanbeyen.movieapplication.dto.PageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/actors")
@RequiredArgsConstructor
public class ActorController {
    private final ActorService actorService;

    @PostMapping
    public ResponseEntity<?> createActor(@RequestBody @Valid CreateActorRequest request) {
        ActorDto createdActor = actorService.createActor(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, createdActor);
    }

    @LogExecutionTime
    @GetMapping({"", "/filter"})
    public ResponseEntity<?> getActors(ActorFilteringOptions actorFilteringOptions, @RequestParam(required = false) OrderBy orderBy, @RequestParam(required = false, defaultValue = DefaultValues.DEFAULT_LIMIT_VALUE) String limit, Pageable pageable) {
        PageDto<Actor, ActorDto> actorDtoList = actorService.filterActors(actorFilteringOptions, orderBy, limit, pageable);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getActor(@PathVariable Integer id) {
        ActorDto actorDto = actorService.getActor(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActor(@PathVariable Integer id, @RequestBody @Valid UpdateActorRequest request) {
        ActorDto actorDto = actorService.updateActor(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable Integer id) {
        String message = actorService.deleteActor(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getMostPopularActors() {
        List<ActorDto> actorDtoList = actorService.getMostPopularActors();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDtoList);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchActors(@RequestParam String fullName) {
        List<ActorDto> actorDtoList = actorService.searchActors(fullName);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDtoList);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> calculateStatistics() {
        Statistics<String, String> statistics = actorService.calculateStatistics();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, statistics);
    }

}
