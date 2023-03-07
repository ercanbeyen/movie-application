package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.response.ResponseHandler;
import com.ercanbeyen.movieapplication.service.CinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cinemas")
@RequiredArgsConstructor
public class CinemaController {
    private final CinemaService cinemaService;

    @PostMapping
    public ResponseEntity<Object> createCinema(@Valid @RequestBody CreateCinemaRequest request) {
        CinemaDto cinemaDto = cinemaService.createCinema(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, cinemaDto);
    }

    @GetMapping
    public ResponseEntity<Object> getCinemas() {
        List<CinemaDto> cinemaDtos = cinemaService.getCinemas();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCinema(@PathVariable("id") String id) {
        CinemaDto cinemaDto = cinemaService.getCinema(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCinema(@PathVariable("id") String id, @Valid @RequestBody UpdateCinemaRequest request) {
        CinemaDto cinemaDto = cinemaService.updateCinema(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCinema(@PathVariable("id") String id) {
        String message = cinemaService.deleteCinema(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }
}
