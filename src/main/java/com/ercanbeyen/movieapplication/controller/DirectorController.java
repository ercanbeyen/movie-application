package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.response.ResponseHandler;
import com.ercanbeyen.movieapplication.service.DirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public ResponseEntity<Object> createDirector(@RequestBody CreateDirectorRequest request) {
        DirectorDto directorDto = directorService.createDirector(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, directorDto);
    }

    @GetMapping
    public ResponseEntity<Object> getDirectors(
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Integer year
    ) {
        List<DirectorDto> directorDtos = directorService.getDirectors(nationality, year);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDirector(@PathVariable("id") Integer id) {
        DirectorDto directorDto = directorService.getDirector(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDirector(@PathVariable("id") Integer id, @RequestBody UpdateDirectorRequest request) {
        DirectorDto directorDto = directorService.updateDirector(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDirector(@PathVariable("id") Integer id) {
        String message = directorService.deleteDirector(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }
}
