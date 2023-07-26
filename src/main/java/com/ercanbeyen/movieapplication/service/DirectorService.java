package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.OrderBy;
import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.option.filter.DirectorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.util.CustomPage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DirectorService {
    DirectorDto createDirector(CreateDirectorRequest request);
    List<DirectorDto> getDirectors(DirectorFilteringOptions filteringOptions, OrderBy orderBy);
    DirectorDto getDirector(Integer id);
    DirectorDto updateDirector(Integer id, UpdateDirectorRequest request);
    String deleteDirector(Integer id);
    List<DirectorDto> getMostPopularDirector();
    List<DirectorDto> searchDirectors(String fullName);
    Director getDirectorById(Integer id);
    CustomPage<DirectorDto, Director> getDirectors(Pageable pageable);
}
