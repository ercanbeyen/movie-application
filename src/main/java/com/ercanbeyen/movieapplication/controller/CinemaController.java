package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.annotation.LogExecutionTime;
import com.ercanbeyen.movieapplication.constant.defaults.DefaultValues;
import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.option.filter.CinemaFilteringOptions;
import com.ercanbeyen.movieapplication.option.search.CinemaSearchOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.service.CinemaService;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.SearchHitDto;
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
    public ResponseEntity<?> createCinema(@RequestBody @Valid CreateCinemaRequest request) {
        CinemaDto cinemaDto = cinemaService.createCinema(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, cinemaDto);
    }

    @LogExecutionTime
    @GetMapping({"", "/filter"})
    public ResponseEntity<?> getCinemas(CinemaFilteringOptions filteringOptions, @RequestParam(required = false, defaultValue = DefaultValues.DEFAULT_LIMIT_VALUE) String limit, Pageable pageable, @RequestHeader(name = "Country", required = false) String country) {
        PageDto<Cinema, CinemaDto> cinemaDtoPage = cinemaService.getCinemas(filteringOptions, limit, pageable, country);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCinema(@PathVariable String id) {
        CinemaDto cinemaDto = cinemaService.getCinema(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCinema(@PathVariable String id, @RequestBody @Valid UpdateCinemaRequest request) {
        CinemaDto cinemaDto = cinemaService.updateCinema(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCinema(@PathVariable String id) {
        String message = cinemaService.deleteCinema(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }

    @GetMapping("/name")
    public ResponseEntity<?> getCinemasByName(@RequestParam("search") String searchTerm) {
        List<SearchHitDto<CinemaDto, Cinema>> searchHits = cinemaService.getCinemasByName(searchTerm);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, searchHits);
    }

    @GetMapping("/address")
    public ResponseEntity<?> getCinemasByAddress(@RequestParam("full-search") String searchTerm) {
        List<SearchHitDto<CinemaDto, Cinema>> searchHits = cinemaService.getCinemasByAddressLike(searchTerm);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, searchHits);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getCinemas(@Valid CinemaSearchOptions searchOptions) {
        List<CinemaDto> cinemaDtoList = cinemaService.searchCinemasByStatus(searchOptions);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDtoList);
    }

    @GetMapping("/halls")
    public ResponseEntity<?> getCinemas(
            @RequestParam(required = false) Integer lower,
            @RequestParam(required = false) Integer higher) {
        List<CinemaDto> cinemaDtoList = cinemaService.findCinemasByHallRange(lower, higher);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, cinemaDtoList);
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        Statistics<String, String> statistics = cinemaService.calculateStatistics();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, statistics);
    }

}
