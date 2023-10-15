package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.message.*;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.dto.converter.CinemaDtoConverter;
import com.ercanbeyen.movieapplication.option.filter.CinemaFilteringOptions;
import com.ercanbeyen.movieapplication.option.search.CinemaSearchOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateCinemaRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateCinemaRequest;
import com.ercanbeyen.movieapplication.document.Cinema;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.CinemaRepository;
import com.ercanbeyen.movieapplication.service.CinemaService;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.SearchHitDto;
import com.ercanbeyen.movieapplication.util.StatisticsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.*;
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
        List<Cinema> cinemas = cinemaRepository.findByStatuses(
                searchOptions.reservation_with_phone(),
                searchOptions.threeD_animation(),
                searchOptions.parking_place(),
                searchOptions.air_conditioning(),
                searchOptions.cafe_food());

        return cinemas.stream()
                .map(cinemaDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public CinemaDto getCinema(String id) {
        Cinema cinemaInDb = findCinemaById(id);
        return cinemaDtoConverter.convert(cinemaInDb);
    }

    @Override
    public CinemaDto updateCinema(String id, UpdateCinemaRequest request) {
        Cinema cinemaInDb = findCinemaById(id);

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
        boolean cinemaExists = cinemaRepository.existsById(id);

        if (!cinemaExists) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE));
        }

        log.info(LogMessages.RESOURCE_FOUND, ResourceNames.CINEMA);
        cinemaRepository.deleteById(id);
        log.info(LogMessages.DELETED, ResourceNames.CINEMA);

        return ResponseMessages.SUCCESS;
    }

    @Override
    public List<SearchHitDto<CinemaDto, Cinema>> getCinemasByName(String searchTerm) {
        List<SearchHit<Cinema>> searchHits = cinemaRepository.findByName(searchTerm)
                .getSearchHits();
        log.info(LogMessages.FETCHED_ALL, ResourceNames.CINEMA);

        return convertSearchHitList(searchHits);
    }

    @Override
    public List<SearchHitDto<CinemaDto, Cinema>> getCinemasByAddressLike(String searchTerm) {
        Criteria criteria = new Criteria("address").expression("*" + searchTerm + "*");
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        List<SearchHit<Cinema>> searchHits = elasticsearchOperations
                .search(criteriaQuery, Cinema.class, IndexCoordinates.of(CINEMA_INDEX))
                .getSearchHits();

        return convertSearchHitList(searchHits);
    }

    @Override
    public PageDto<Cinema, CinemaDto> getCinemas(CinemaFilteringOptions filteringOptions, String limit, Pageable pageable, String country) {
        Page<Cinema> cinemaPage = cinemaRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.CINEMA);

        boolean isCountryBlank = StringUtils.isBlank(country);

        if (isCountryBlank) {
            log.info(LogMessages.REQUEST_HEADER_FIELD_NULL, "Country");
        }

        Predicate<Cinema> cinemaPredicate = (cinema) -> (isCountryBlank || cinema.getCountry().equals(country))
                && (StringUtils.isBlank(filteringOptions.city()) || cinema.getCity().equals(filteringOptions.city()))
                && (filteringOptions.reservation_with_phone() == null || cinema.isReservation_with_phone() == filteringOptions.reservation_with_phone())
                && (filteringOptions.threeD_animation() == null || cinema.isThreeD_animation() == filteringOptions.threeD_animation())
                && (filteringOptions.parking_place() == null || cinema.isParking_place() == filteringOptions.parking_place())
                && (filteringOptions.air_conditioning() == null || cinema.isAir_conditioning() == filteringOptions.air_conditioning())
                && (filteringOptions.cafe_food() == null || cinema.isCafe_food() == filteringOptions.cafe_food());

        long maximumSize = Long.parseLong(limit);

        List<CinemaDto> cinemaDtoList = cinemaPage.stream()
                .filter(cinemaPredicate)
                .limit(maximumSize)
                .map(cinemaDtoConverter::convert)
                .toList();

        return new PageDto<>(cinemaPage, cinemaDtoList);

    }

    @Override
    public List<CinemaDto> findCinemasByHallRange(Integer lower, Integer higher) {
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

    @Override
    public Statistics<String, String> calculateStatistics() {
        Map<String, String> statisticsMap = new HashMap<>();
        List<Cinema> cinemaList = StatisticsUtil.convertIterableToList(cinemaRepository.findAll());

        Comparator<Cinema> cinemaComparator = Comparator.comparing(Cinema::getNumberOfHalls);

        String nameOfMostHallsHad = cinemaList.stream()
                .max(cinemaComparator)
                .map(Cinema::getName)
                .orElse(StatisticsMessages.NOT_EXISTS);

        statisticsMap.put("mostHallsHad", nameOfMostHallsHad);

        String nameOfLeastHallsHad = cinemaList.stream()
                .min(cinemaComparator)
                .map(Cinema::getName)
                .orElse(StatisticsMessages.NOT_EXISTS);

        statisticsMap.put("leastHallsHad", nameOfLeastHallsHad);

        List<String> countryList = cinemaList.stream()
                .map(Cinema::getCountry)
                .toList();

        String mostOccurredCountry = StatisticsUtil.calculateMostOccurred(countryList);
        mostOccurredCountry = StatisticsUtil.valueAssignmentToStringItem(mostOccurredCountry);
        statisticsMap.put("mostOccurredCountry", mostOccurredCountry);

        String leastOccurredCountry = StatisticsUtil.calculateLeastOccurred(countryList);
        leastOccurredCountry = StatisticsUtil.valueAssignmentToStringItem(leastOccurredCountry);
        statisticsMap.put("leastOccurredCountry", leastOccurredCountry);


        List<String> cityList = cinemaList.stream()
                .map(Cinema::getCity)
                .toList();

        String mostOccurredCity = StatisticsUtil.calculateMostOccurred(cityList);
        mostOccurredCity = StatisticsUtil.valueAssignmentToStringItem(mostOccurredCity);
        statisticsMap.put("mostOccurredCity", mostOccurredCity);

        String leastOccurredCity = StatisticsUtil.calculateLeastOccurred(cityList);
        leastOccurredCity = StatisticsUtil.valueAssignmentToStringItem(leastOccurredCity);
        statisticsMap.put("leastOccurredCity", leastOccurredCity);

        return new Statistics<>(ResourceNames.CINEMA, statisticsMap);
    }


    public List<SearchHitDto<CinemaDto, Cinema>> convertSearchHitList(List<SearchHit<Cinema>> searchHits) {
        List<SearchHitDto<CinemaDto, Cinema>> searchHitDtoList = new ArrayList<>();

        searchHits.forEach(
                searchHit -> {
                    CinemaDto cinemaDto = cinemaDtoConverter.convert(searchHit.getContent());
                    SearchHitDto<CinemaDto, Cinema> searchHitDto = new SearchHitDto<>(searchHit, cinemaDto);
                    searchHitDtoList.add(searchHitDto);
                }
        );

        return searchHitDtoList;
    }

    private Cinema findCinemaById(String id) {
        Optional<Cinema> optionalCinema = cinemaRepository.findById(id);

        if (optionalCinema.isEmpty()) {
            log.error(LogMessages.RESOURCE_NOT_FOUND, ResourceNames.CINEMA, id);
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.CINEMA));
        }

        log.info(LogMessages.RESOURCE_FOUND, ResourceNames.CINEMA, id);
        return optionalCinema.get();
    }
}
