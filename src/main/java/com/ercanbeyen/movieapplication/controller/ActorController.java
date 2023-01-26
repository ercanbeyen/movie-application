package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.dto.response.ResponseHandler;
import com.ercanbeyen.movieapplication.service.ActorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Object> createActor(@Valid @RequestBody CreateActorRequest request) {
        ActorDto createdActor = actorService.createActor(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, createdActor);
    }

    @GetMapping
    public ResponseEntity<Object> getActors(
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer movieId) {
        List<ActorDto> actorDtos = actorService.getActors(nationality, year, movieId);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getActor(@PathVariable("id") Integer id) {
        ActorDto actorDto = actorService.getActor(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateActor(@PathVariable("id") Integer id, @Valid @RequestBody UpdateActorRequest request) {
        ActorDto actorDto = actorService.updateActor(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteActor(@PathVariable("id") Integer id) {
        String message = actorService.deleteActor(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchActors(@RequestParam String fullName) {
        List<ActorDto> actorDtos = actorService.searchActors(fullName);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, actorDtos);
    }

}
