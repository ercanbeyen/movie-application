package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.entity.Director;

import java.time.LocalDate;
import java.util.List;

public interface DirectorService {
    DirectorDto createDirector(CreateDirectorRequest request);
    List<DirectorDto> getDirectors(String nationality, Integer year);
    DirectorDto getDirector(Integer id);
    DirectorDto updateDirector(Integer id, UpdateDirectorRequest request);
    String deleteDirector(Integer id);
    Director getDirectorById(Integer id);
}
