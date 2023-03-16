package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.service.DirectorService;
import com.ercanbeyen.movieapplication.util.CustomPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public ResponseEntity<Object> createDirector(@Valid @RequestBody CreateDirectorRequest request) {
        DirectorDto directorDto = directorService.createDirector(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, null, directorDto);
    }

    @GetMapping("/filter")
    public ResponseEntity<Object> getDirectors(
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Boolean sort,
            @RequestParam(value = "desc", required = false) Boolean descending,
            @RequestParam(required = false) Integer limit) {
        List<DirectorDto> directorDtos = directorService.getDirectors(nationality, year, sort, descending, limit);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDirector(@PathVariable("id") Integer id) {
        DirectorDto directorDto = directorService.getDirector(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDirector(@PathVariable("id") Integer id, @Valid @RequestBody UpdateDirectorRequest request) {
        DirectorDto directorDto = directorService.updateDirector(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDirector(@PathVariable("id") Integer id) {
        String message = directorService.deleteDirector(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }

    @GetMapping("/popular")
    public ResponseEntity<Object> getMostPopularDirectors() {
        List<DirectorDto> directorDtos = directorService.getMostPopularDirector();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorDtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchDirectors(@RequestParam String fullName) {
        List<DirectorDto> directorDtos = directorService.searchDirectors(fullName);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorDtos);
    }

    @GetMapping
    public ResponseEntity<Object> getDirectors(Pageable pageable) {
        CustomPage<DirectorDto, Director> directorPage = directorService.getDirectors(pageable);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, directorPage);
    }
}
