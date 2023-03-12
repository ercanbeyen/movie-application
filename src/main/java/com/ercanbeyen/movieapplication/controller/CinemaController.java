package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.response.ResponseHandler;
import com.ercanbeyen.movieapplication.service.CinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
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
    public ResponseEntity<Object> getCinemas(
            @RequestParam("reservation") boolean reservation_with_phone,
            @RequestParam("three-D") boolean threeD_animation,
            @RequestParam("parking") boolean parking_place,
            @RequestParam("air-conditioning") boolean air_conditioning,
            @RequestParam("cafe") boolean cafe_food) {
        List<CinemaDto> cinemaDtos = cinemaService.getCinemas(reservation_with_phone, threeD_animation, parking_place, air_conditioning, cafe_food);
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

    @GetMapping("/full-search")
    public ResponseEntity<Object> getCinemasByName(@RequestParam("search") String searchTerm) {
        List<SearchHit<Cinema>> cinemas = cinemaService.getCinemasByName(searchTerm);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemas);
    }

}
