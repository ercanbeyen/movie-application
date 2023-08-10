package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.message.*;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.converter.CinemaDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.CinemaFilteringOptions;
import com.ercanbeyen.movieapplication.dto.option.search.CinemaSearchOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.exception.ResourceNotFound;
import com.ercanbeyen.movieapplication.repository.CinemaRepository;
import com.ercanbeyen.movieapplication.service.CinemaService;
import com.ercanbeyen.movieapplication.util.CustomPage;
import com.ercanbeyen.movieapplication.util.CustomSearchHit;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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
        log.info(LogMessages.STARTED, "createCinema");

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

        Cinema savedCinema = cinemaRepository.save(newCinema);
        log.info(LogMessages.SAVED, ResourceNames.CINEMA);

        return cinemaDtoConverter.convert(savedCinema);
    }

    @Override
    public List<CinemaDto> searchCinemasByStatus(CinemaSearchOptions searchOptions) {
        log.info(LogMessages.STARTED, "searchCinemasByStatus");
        List<Cinema> cinemas = cinemaRepository.findByStatuses(
                searchOptions.getReservation_with_phone(),
                searchOptions.getThreeD_animation(),
                searchOptions.getParking_place(),
                searchOptions.getAir_conditioning(),
                searchOptions.getCafe_food());
        log.info(LogMessages.FETCHED, ResourceNames.CINEMA);

        return cinemas.stream()
                .map(cinemaDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public CinemaDto getCinema(String id) {
        log.info(LogMessages.STARTED, "getCinema");
        Cinema cinemaInDb = findCinemaById(id);
        log.info(LogMessages.FETCHED, ResourceNames.CINEMA);
        return cinemaDtoConverter.convert(cinemaInDb);
    }

    @Override
    public CinemaDto updateCinema(String id, UpdateCinemaRequest request) {
        log.info(LogMessages.STARTED, "updateCinema");
        Cinema cinemaInDb = findCinemaById(id);
        log.info(LogMessages.FETCHED, ResourceNames.CINEMA);

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
        log.info(LogMessages.FIELDS_SET);

        Cinema savedCinema = cinemaRepository.save(cinemaInDb);
        log.info(LogMessages.SAVED, ResourceNames.CINEMA);

        return cinemaDtoConverter.convert(savedCinema);
    }

    @Override
    public String deleteCinema(String id) {
        log.info(LogMessages.STARTED, "deleteCinema");
        boolean cinemaExists = cinemaRepository.existsById(id);

        if (!cinemaExists) {
            throw new ResourceNotFound(String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE, id));
        }

        log.info(LogMessages.EXISTS, ResourceNames.CINEMA);
        cinemaRepository.deleteById(id);
        log.info(LogMessages.DELETED, ResourceNames.CINEMA);

        return String.format(ResponseMessages.SUCCESS, ResourceNames.CINEMA, id, ActionNames.DELETED);
    }

    @Override
    public List<CustomSearchHit<CinemaDto, Cinema>> getCinemasByName(String searchTerm) {
        log.info(LogMessages.STARTED, "getCinemasByName");

        List<SearchHit<Cinema>> searchHits = cinemaRepository.findByName(searchTerm)
                .getSearchHits();
        log.info(LogMessages.FETCHED_ALL, ResourceNames.CINEMA);

        return convertSearchHitList(searchHits);
    }

    @Override
    public List<CustomSearchHit<CinemaDto, Cinema>> getCinemasByAddressLike(String searchTerm) {
        log.info(LogMessages.STARTED, "getCinemasByAddressLike");

        Criteria criteria = new Criteria("address").expression("*" + searchTerm + "*");
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        List<SearchHit<Cinema>> searchHits = elasticsearchOperations
                .search(criteriaQuery, Cinema.class, IndexCoordinates.of(CINEMA_INDEX))
                .getSearchHits();

        return convertSearchHitList(searchHits);
    }

    @Override
    public CustomPage<Cinema, CinemaDto> filterCinemas(CinemaFilteringOptions filteringOptions, Pageable pageable) {
        log.info(LogMessages.STARTED, "filterCinemas");

        Page<Cinema> cinemaPage = cinemaRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.CINEMA);

        Predicate<Cinema> cinemaPredicate = (cinema) -> ((filteringOptions.getCountry() == null || cinema.getCountry().equals(filteringOptions.getCountry()))
                && (filteringOptions.getCity() == null || cinema.getCity().equals(filteringOptions.getCity()))
                && (filteringOptions.getReservation_with_phone() == null || cinema.isReservation_with_phone() == filteringOptions.getReservation_with_phone())
                && (filteringOptions.getThreeD_animation() == null || cinema.isThreeD_animation() == filteringOptions.getThreeD_animation())
                && (filteringOptions.getParking_place() == null || cinema.isParking_place() == filteringOptions.getParking_place())
                && (filteringOptions.getAir_conditioning() == null || cinema.isAir_conditioning() == filteringOptions.getAir_conditioning())
                && (filteringOptions.getCafe_food() == null || cinema.isCafe_food() == filteringOptions.getCafe_food()));

        if (filteringOptions.getLimit() == null) {
            log.info(LogMessages.PARAMETER_NULL, ParameterNames.LIMIT);
            filteringOptions.setLimit(cinemaRepository.count());
        }

        List<CinemaDto> cinemaDtoList = cinemaPage.stream()
                .filter(cinemaPredicate)
                .limit(filteringOptions.getLimit())
                .map(cinemaDtoConverter::convert)
                .toList();

        return new CustomPage<>(cinemaPage, cinemaDtoList);

    }

    @Override
    public List<CinemaDto> findCinemasByHallRange(Integer lower, Integer higher) {
        log.info(LogMessages.STARTED, "findCinemasByHallRange");
        List<Cinema> cinemas = new ArrayList<>();

        if (lower != null && higher != null) {
            cinemas = cinemaRepository.findByNumberOfHallsBetween(lower, higher);
        } else if (lower != null) {
            cinemas = cinemaRepository.findByNumberOfHallsGreaterThanEqual(lower);
        } else if (higher != null) {
            cinemas = cinemaRepository.findByNumberOfHallsLessThanEqual(higher);
        } else {
            Iterable<Cinema> cinemaIterable = cinemaRepository.findAll();
            cinemaIterable.forEach(cinemas::add);
        }

        return cinemas.stream()
                .map(cinemaDtoConverter::convert)
                .collect(Collectors.toList());
    }


    public List<CustomSearchHit<CinemaDto, Cinema>> convertSearchHitList(List<SearchHit<Cinema>> searchHits) {
        log.info(LogMessages.STARTED, "convertSearchHitList");
        List<CustomSearchHit<CinemaDto, Cinema>> customSearchHits = new ArrayList<>();

        searchHits.forEach(
                searchHit -> {
                    CinemaDto cinemaDto = cinemaDtoConverter.convert(searchHit.getContent());
                    CustomSearchHit<CinemaDto, Cinema> customSearchHit = new CustomSearchHit<>(searchHit, cinemaDto);
                    customSearchHits.add(customSearchHit);
                }
        );

        return customSearchHits;
    }

    private Cinema findCinemaById(String id) {
        return cinemaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound(String.format(ResponseMessages.NOT_FOUND, ResourceNames.CINEMA, id)));
    }
}
