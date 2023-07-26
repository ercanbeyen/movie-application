package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.option.filter.CinemaFilteringOptions;
import com.ercanbeyen.movieapplication.dto.option.search.CinemaSearchOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.service.CinemaService;
import com.ercanbeyen.movieapplication.util.CustomPage;
import com.ercanbeyen.movieapplication.util.CustomSearchHit;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Object> createCinema(@RequestBody @Valid CreateCinemaRequest request) {
        CinemaDto cinemaDto = cinemaService.createCinema(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, cinemaDto);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> getCinemas(CinemaFilteringOptions filteringOptions) {
        List<CinemaDto> cinemaDtoList = cinemaService.filterCinemas(filteringOptions);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCinema(@PathVariable("id") String id) {
        CinemaDto cinemaDto = cinemaService.getCinema(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCinema(@PathVariable("id") String id, @RequestBody @Valid UpdateCinemaRequest request) {
        CinemaDto cinemaDto = cinemaService.updateCinema(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCinema(@PathVariable("id") String id) {
        String message = cinemaService.deleteCinema(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }

    @GetMapping("/name")
    public ResponseEntity<Object> getCinemasByName(@RequestParam("search") String searchTerm) {
        List<CustomSearchHit<CinemaDto, Cinema>> searchHits = cinemaService.getCinemasByName(searchTerm);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, searchHits);
    }

    @GetMapping("/address")
    public ResponseEntity<Object> getCinemasByAddress(@RequestParam("full-search") String searchTerm) {
        List<CustomSearchHit<CinemaDto, Cinema>> searchHits = cinemaService.getCinemasByAddressLike(searchTerm);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, searchHits);
    }

    @GetMapping
    public ResponseEntity<Object> getCinemas(Pageable pageable) {
        CustomPage<CinemaDto, Cinema> cinemaPage = cinemaService.pagination(pageable);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getCinemas(@Valid CinemaSearchOptions searchOptions) {
        List<CinemaDto> cinemaDtoList = cinemaService.searchCinemasByStatus(searchOptions);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDtoList);
    }

    @GetMapping("/halls")
    public ResponseEntity<Object> getCinemas(
            @RequestParam(required = false) Integer lower,
            @RequestParam(required = false) Integer higher) {
        List<CinemaDto> cinemaDtoList = cinemaService.findCinemasByHallRange(lower, higher);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDtoList);
    }
}
