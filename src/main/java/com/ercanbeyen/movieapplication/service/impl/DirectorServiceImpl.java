package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.*;
import com.ercanbeyen.movieapplication.constant.names.ParameterNames;
import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.dto.converter.DirectorDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.DirectorFilteringOptions;
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
        log.info(LogMessages.STARTED, "createDirector");

        Director newDirector = Director.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .moviesDirected(new ArrayList<>())
                .build();

        Director savedDirector = directorRepository.save(newDirector);
        log.info(LogMessages.SAVED, ResourceNames.DIRECTOR);

        return directorDtoConverter.convert(savedDirector);
    }

    @Override
    public PageDto<Director, DirectorDto> filterDirectors(DirectorFilteringOptions filteringOptions, OrderBy orderBy, String limit, Pageable pageable) {
        log.info(LogMessages.STARTED, "filterDirectors");
        Page<Director> directorPage = directorRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.DIRECTOR);

        Predicate<Director> directorPredicate = (director) -> ((StringUtils.isBlank(filteringOptions.getNationality()) || director.getNationality().equals(filteringOptions.getNationality()))
                && (filteringOptions.getBirthYear() == null || director.getBirthYear().getYear() == filteringOptions.getBirthYear()));

        long maximumSize = Long.parseLong(limit);

        if (orderBy == null) {
            log.info(LogMessages.REQUEST_PARAMETER_NULL, ParameterNames.ORDER_BY);

            List<DirectorDto> directorDtoList = directorPage.stream()
                    .filter(directorPredicate)
                    .limit(maximumSize)
                    .map(directorDtoConverter::convert)
                    .toList();

            return new PageDto<>(directorPage, directorDtoList);
        }

        Comparator<Director> directorComparator = Comparator.comparing(director -> director.getMoviesDirected().size());

        log.info(LogMessages.ORDER_BY_VALUE, orderBy.name());

        if (orderBy == OrderBy.DESC) {
            directorComparator = directorComparator.reversed();
        }

        List<DirectorDto> directorList = directorPage.stream()
                .filter(directorPredicate)
                .sorted(directorComparator)
                .limit(maximumSize)
                .map(directorDtoConverter::convert)
                .toList();

        return new PageDto<>(directorPage, directorList);
    }

    @Cacheable(value = "directors", key = "#id", unless = "#result.moviesDirected.size() < 2")
    @Override
    public DirectorDto getDirector(Integer id) {
        log.info(LogMessages.STARTED, "getDirector");
        Director directorInDb = findDirectorById(id);
        log.info(LogMessages.FETCHED, ResourceNames.DIRECTOR);
        return directorDtoConverter.convert(directorInDb);
    }

    @CacheEvict(value = "directors", allEntries = true)
    @Override
    public DirectorDto updateDirector(Integer id, UpdateDirectorRequest request) {
        log.info(LogMessages.STARTED, "updateDirector");

        Director directorInDb = findDirectorById(id);
        log.info(LogMessages.FETCHED, ResourceNames.DIRECTOR);

        directorInDb.setName(request.getName());
        directorInDb.setSurname(request.getSurname());
        directorInDb.setNationality(request.getNationality());
        directorInDb.setBirthYear(request.getBirthYear());
        directorInDb.setBiography(request.getBiography());
        log.info(LogMessages.FIELDS_SET);

        Director savedDirector = directorRepository.save(directorInDb);
        log.info(LogMessages.SAVED, ResourceNames.DIRECTOR);

        return directorDtoConverter.convert(savedDirector);
    }

    @CacheEvict(value = "directors", key = "#id")
    @Override
    public String deleteDirector(Integer id) {
        log.info(LogMessages.STARTED, "deleteDirector");

        boolean directorExists = directorRepository.existsById(id);

        if (!directorExists) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.DIRECTOR, id));
        }

        log.info(LogMessages.EXISTS, ResourceNames.DIRECTOR);
        directorRepository.deleteById(id);
        log.info(LogMessages.DELETED, ResourceNames.DIRECTOR);

        return String.format(ResponseMessages.SUCCESS, ResourceNames.DIRECTOR, id, ActionMessages.DELETED);
    }

    @Cacheable(value = "directors")
    @Override
    public List<DirectorDto> getMostPopularDirectors() {
        log.info(LogMessages.STARTED, "getMostPopularDirectors");

        List<Director> directors = directorRepository.findAll();
        log.info(LogMessages.FETCHED_ALL, ResourceNames.DIRECTOR);
        int numberOfMovies = 2;

        return directors.stream()
                .filter(director -> director.getMoviesDirected().size() >= numberOfMovies)
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<DirectorDto> searchDirectors(String fullName) {
        log.info(LogMessages.STARTED, "searchDirectors");

        List<Director> directors = directorRepository.findByFullName(fullName);
        log.info(LogMessages.SAVED, ResourceNames.DIRECTOR);

        return directors.stream()
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Director findDirectorById(Integer id) {
        log.info(LogMessages.STARTED, "findDirectorById");
        return directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.DIRECTOR, id)));
    }

    @Override
    public Statistics<String, String> calculateStatistics() {
        log.info(LogMessages.STARTED, LogMessages.CALCULATE_STATISTICS);

        Statistics<String, String> statistics = new Statistics<>();
        statistics.setTopic("Director");

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

        statistics.setResult(statisticsMap);

        return statistics;
    }

}
