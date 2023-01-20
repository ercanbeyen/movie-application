package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.service.DirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public ResponseEntity<Object> createDirector(@RequestBody CreateDirectorRequest request) {
        DirectorDto createdDirector = directorService.createDirector(request);
        return new ResponseEntity<>(createdDirector, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Object> getDirectors() {
        return ResponseEntity.ok(directorService.getDirectors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDirector(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(directorService.getDirector(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDirector(@PathVariable("id") Integer id, @RequestBody UpdateDirectorRequest request) {
        return ResponseEntity.ok(directorService.updateDirector(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDirector(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(directorService.deleteDirector(id));
    }
}
