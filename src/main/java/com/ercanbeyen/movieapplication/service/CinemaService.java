package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.option.filter.CinemaFilteringOptions;
import com.ercanbeyen.movieapplication.dto.option.search.CinemaSearchOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.util.CustomPage;
import com.ercanbeyen.movieapplication.util.CustomSearchHit;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CinemaService {
    CinemaDto createCinema(CreateCinemaRequest request);
    CustomPage<Cinema, CinemaDto> filterCinemas(CinemaFilteringOptions filteringOptions, Pageable pageable);
    CinemaDto getCinema(String id);
    CinemaDto updateCinema(String id, UpdateCinemaRequest request);
    String deleteCinema(String id);
    List<CustomSearchHit<CinemaDto, Cinema>> getCinemasByName(String searchTerm);
    List<CustomSearchHit<CinemaDto, Cinema>> getCinemasByAddressLike(String searchTerm);
    List<CinemaDto> searchCinemasByStatus(CinemaSearchOptions searchOptions);
    List<CinemaDto> findCinemasByHallRange(Integer lower, Integer higher);
}
