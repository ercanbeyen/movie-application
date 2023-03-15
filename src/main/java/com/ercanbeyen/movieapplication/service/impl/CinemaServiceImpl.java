package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.converter.CinemaDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.exception.EntityNotFound;
import com.ercanbeyen.movieapplication.repository.CinemaRepository;
import com.ercanbeyen.movieapplication.service.CinemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CinemaServiceImpl implements CinemaService {
    private final CinemaRepository cinemaRepository;
    private final CinemaDtoConverter cinemaDtoConverter;
    private final ElasticsearchOperations elasticsearchOperations;
    private static final String CINEMA_INDEX = "cinema";

    @Override
    public CinemaDto createCinema(CreateCinemaRequest request) {
        Cinema newCinema = Cinema.builder()
                .name(request.getName())
                .country(request.getCountry())
                .city(request.getCity())
                .address(request.getAddress())
                .numberOfHalls(request.getNumberOfHalls())
                .contactNumber(request.getContactNumber())
                .air_conditioning(request.isAir_conditioning())
                .cafe_food(request.isCafe_food())
                .threeD_animation(request.isThreeD_animation())
                .parking_place(request.isParking_place())
                .reservation_with_phone(request.isReservation_with_phone())
                .build();

        return cinemaDtoConverter.convert(cinemaRepository.save(newCinema));
    }

    @Override
    public List<CinemaDto> getCinemas(boolean reservation_with_phone, boolean threeD_animation, boolean parking_place, boolean air_conditioning, boolean cafe_food) {
        List<Cinema> cinemas = cinemaRepository.findByStatuses(reservation_with_phone, threeD_animation, parking_place, air_conditioning, cafe_food);

        return cinemas.stream()
                .map(cinemaDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public CinemaDto getCinema(String id) {
        Cinema cinemaInDb = cinemaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Cinema " + id + " is not found"));

        return cinemaDtoConverter.convert(cinemaInDb);
    }

    @Override
    public CinemaDto updateCinema(String id, UpdateCinemaRequest request) {
        Cinema cinemaInDb = cinemaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Cinema " + id + " is not found"));

        cinemaInDb.setName(request.getName());
        cinemaInDb.setCountry(request.getCountry());
        cinemaInDb.setCity(request.getCity());
        cinemaInDb.setAddress(request.getAddress());
        cinemaInDb.setContactNumber(request.getContactNumber());
        cinemaInDb.setNumberOfHalls(request.getNumberOfHalls());
        cinemaInDb.setAir_conditioning(request.isAir_conditioning());
        cinemaInDb.setCafe_food(request.isCafe_food());
        cinemaInDb.setParking_place(request.isParking_place());
        cinemaInDb.setThreeD_animation(request.isThreeD_animation());
        cinemaInDb.setReservation_with_phone(request.isReservation_with_phone());

        return cinemaDtoConverter.convert(cinemaRepository.save(cinemaInDb));
    }

    @Override
    public String deleteCinema(String id) {
        cinemaRepository.deleteById(id);
        return "Cinema " + id + " is successfully deleted";
    }

    @Override
    public List<SearchHit<Cinema>> getCinemasByName(String searchTerm) {
        return cinemaRepository.findByName(searchTerm).getSearchHits();
    }

    @Override
    public List<SearchHit<Cinema>> getCinemasByAddressLike(String searchTerm) {
        Criteria criteria = new Criteria("address").expression("*" + searchTerm + "*");
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        return elasticsearchOperations
                .search(criteriaQuery, Cinema.class, IndexCoordinates.of(CINEMA_INDEX))
                .getSearchHits();
    }

    @Override
    public Page<Cinema> getCinemas(Pageable pageable) {
        return cinemaRepository.findAll(pageable);
    }


}
