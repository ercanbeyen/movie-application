package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.ActorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.service.ActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/actors")
@RequiredArgsConstructor
public class ActorController {
    private final ActorService actorService;

    @PostMapping
    public ResponseEntity<Object> createActor(@RequestBody CreateActorRequest request) {
        ActorDto createdActor = actorService.createActor(request);
        return new ResponseEntity<>(createdActor, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Object> getActors() {
        return ResponseEntity.ok(actorService.getActors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getActor(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(actorService.getActor(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateActor(@PathVariable("id") Integer id, @RequestBody UpdateActorRequest request) {
        return ResponseEntity.ok(actorService.updateActor(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteActor(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(actorService.deleteActor(id));
    }

}
