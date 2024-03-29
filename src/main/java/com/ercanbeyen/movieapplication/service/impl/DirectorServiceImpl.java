package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.defaults.DefaultValues;
import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.*;
import com.ercanbeyen.movieapplication.constant.names.ParameterNames;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.dto.converter.DirectorDtoConverter;
import com.ercanbeyen.movieapplication.option.filter.DirectorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.DirectorRepository;
import com.ercanbeyen.movieapplication.service.DirectorService;
import com.ercanbeyen.movieapplication.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private final DirectorDtoConverter directorDtoConverter;

    @Override
    public DirectorDto createDirector(CreateDirectorRequest request) {
        Director newDirector = Director.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthDate(request.getBirthDate())
                .biography(request.getBiography())
                .moviesDirected(new ArrayList<>())
                .build();

        Director savedDirector = directorRepository.save(newDirector);
        log.info(LogMessages.SAVED, ResourceNames.DIRECTOR);

        return directorDtoConverter.convert(savedDirector);
    }

    @Override
    public PageDto<Director, DirectorDto> getDirectors(DirectorFilteringOptions filteringOptions, OrderBy orderBy, String limit, Pageable pageable) {
        Page<Director> directorPage = directorRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.DIRECTOR);

        Predicate<Director> directorPredicate = (director) -> ((StringUtils.isBlank(filteringOptions.nationality()) || director.getNationality().equals(filteringOptions.nationality()))
                && (filteringOptions.birthYear() == null || director.getBirthDate().getYear() == filteringOptions.birthYear()));

        long maximumSize = Long.parseLong(limit);
        List<DirectorDto> directorDtoList;

        if (orderBy == null) {
            log.info(LogMessages.REQUEST_PARAMETER_NULL, ParameterNames.ORDER_BY);

            directorDtoList = directorPage.stream()
                    .filter(directorPredicate)
                    .limit(maximumSize)
                    .map(directorDtoConverter::convert)
                    .toList();
        } else {
            log.info(LogMessages.ORDER_BY_VALUE, orderBy.getOrderByInfo());
            Comparator<Director> directorAscendingComparator = Comparator.comparing(director -> director.getMoviesDirected().size());

            Comparator<Director> directorComparator = switch (orderBy) {
                case ASC -> directorAscendingComparator;
                case DESC -> directorAscendingComparator.reversed();
            };

            directorDtoList = directorPage.stream()
                    .filter(directorPredicate)
                    .sorted(directorComparator)
                    .limit(maximumSize)
                    .map(directorDtoConverter::convert)
                    .toList();
        }

        return new PageDto<>(directorPage, directorDtoList);
    }

    @Cacheable(value = "directors", key = "#id", unless = "#result.moviesDirected.size() < 2")
    @Override
    public DirectorDto getDirector(Integer id) {
        Director directorInDb = findDirectorById(id);
        return directorDtoConverter.convert(directorInDb);
    }

    @CacheEvict(value = "directors", allEntries = true)
    @Override
    public DirectorDto updateDirector(Integer id, UpdateDirectorRequest request) {
        Director directorInDb = findDirectorById(id);

        directorInDb.setName(request.getName());
        directorInDb.setSurname(request.getSurname());
        directorInDb.setNationality(request.getNationality());
        directorInDb.setBirthDate(request.getBirthDate());
        directorInDb.setBiography(request.getBiography());
        log.info(LogMessages.FIELDS_SET);

        Director savedDirector = directorRepository.save(directorInDb);
        log.info(LogMessages.SAVED, ResourceNames.DIRECTOR);

        return directorDtoConverter.convert(savedDirector);
    }

    @CacheEvict(value = "directors", key = "#id")
    @Override
    public String deleteDirector(Integer id) {
        directorRepository.findById(id)
                .ifPresentOrElse(directorRepository::delete, () -> {
                    throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.DIRECTOR));
                });

        log.info(LogMessages.DELETED, ResourceNames.DIRECTOR);
        return ResponseMessages.SUCCESS;
    }

    @Cacheable(value = "directors")
    @Override
    public List<DirectorDto> getMostPopularDirectors() {
        log.info(LogMessages.STARTED, "getMostPopularDirectors");

        List<Director> directors = directorRepository.findAll();
        log.info(LogMessages.FETCHED_ALL, ResourceNames.DIRECTOR);

        return directors.stream()
                .filter(director -> director.getMoviesDirected().size() >= DefaultValues.MINIMUM_NUMBER_OF_MOVIES_TO_BECOME_POPULAR)
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<DirectorDto> searchDirectors(String fullName) {
        List<Director> directors = directorRepository.findByFullName(fullName);
        log.info(LogMessages.SAVED, ResourceNames.DIRECTOR);

        return directors.stream()
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Director findDirector(Integer id) {
        return findDirectorById(id);
    }

    @Override
    public Statistics<String, String> calculateStatistics() {
        Map<String, String> statisticsMap = new HashMap<>();
        List<Director> directorList = directorRepository.findAll();

        DoubleSummaryStatistics summaryStatistics = directorList.stream()
                .mapToDouble(director -> director.getMoviesDirected().size())
                .summaryStatistics();

        statisticsMap.put("mostDirectedCount", String.valueOf(summaryStatistics.getMax()));
        statisticsMap.put("leastDirectedCount", String.valueOf(summaryStatistics.getMin()));
        statisticsMap.put("directedMovieSum", String.valueOf(summaryStatistics.getSum()));
        statisticsMap.put("directedMovieAverage", String.valueOf(summaryStatistics.getAverage()));
        statisticsMap.put("directorCount", String.valueOf(summaryStatistics.getCount()));

        return new Statistics<>(ResourceNames.DIRECTOR, statisticsMap);
    }

    private Director findDirectorById(Integer id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.DIRECTOR)));
    }
}
