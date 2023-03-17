package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.util.CustomPage;
import com.ercanbeyen.movieapplication.util.CustomSearchHit;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CinemaService {
    CinemaDto createCinema(CreateCinemaRequest request);
    List<CinemaDto> getCinemas(boolean reservation_with_phone, boolean threeD_animation, boolean parking_place, boolean air_conditioning, boolean cafe_food);
    CinemaDto getCinema(String id);
    CinemaDto updateCinema(String id, UpdateCinemaRequest request);
    String deleteCinema(String id);
    List<CustomSearchHit<CinemaDto, Cinema>> getCinemasByName(String searchTerm);
    List<CustomSearchHit<CinemaDto, Cinema>> getCinemasByAddressLike(String searchTerm);
    CustomPage<CinemaDto, Cinema> getCinemas(Pageable pageable);
    List<CinemaDto> getCinemas(String country, String city, Boolean reservation_with_phone, Boolean threeD_animation, Boolean parking_place, Boolean air_conditioning, Boolean cafe_food);
}
