package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

public interface CinemaService {
    CinemaDto createCinema(CreateCinemaRequest request);
    List<CinemaDto> getCinemas(boolean reservation_with_phone, boolean threeD_animation, boolean parking_place, boolean air_conditioning, boolean cafe_food);
    CinemaDto getCinema(String id);
    CinemaDto updateCinema(String id, UpdateCinemaRequest request);
    String deleteCinema(String id);
    List<SearchHit<Cinema>> getCinemasByName(String searchTerm);
    List<SearchHit<Cinema>> getCinemasByAddressLike(String searchTerm);
}
