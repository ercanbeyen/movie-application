package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.option.filter.DirectorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.dto.PageDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DirectorService {
    DirectorDto createDirector(CreateDirectorRequest request);
    PageDto<Director, DirectorDto> getDirectors(DirectorFilteringOptions filteringOptions, OrderBy orderBy, String limit, Pageable pageable);
    DirectorDto getDirector(Integer id);
    DirectorDto updateDirector(Integer id, UpdateDirectorRequest request);
    String deleteDirector(Integer id);
    List<DirectorDto> getMostPopularDirectors();
    List<DirectorDto> searchDirectors(String fullName);
    Director findDirectorById(Integer id);
    Statistics<String, String> calculateStatistics();
}
