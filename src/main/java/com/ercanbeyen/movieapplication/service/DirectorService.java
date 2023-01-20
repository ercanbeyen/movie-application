package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;

import java.util.List;

public interface DirectorService {
    DirectorDto createDirector(CreateDirectorRequest request);
    List<DirectorDto> getDirectors();
    DirectorDto getDirector(Integer id);
    DirectorDto updateDirector(Integer id, UpdateDirectorRequest request);
    String deleteDirector(Integer id);
}
