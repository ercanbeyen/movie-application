package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.*;
import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.converter.DirectorDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.DirectorFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.exception.EntityNotFound;
import com.ercanbeyen.movieapplication.repository.DirectorRepository;
import com.ercanbeyen.movieapplication.service.DirectorService;
import com.ercanbeyen.movieapplication.util.CustomPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        log.info(LogMessages.SAVED, EntityNames.DIRECTOR);

        return directorDtoConverter.convert(savedDirector);
    }

    @Override
    public CustomPage<Director, DirectorDto> filterDirectors(DirectorFilteringOptions filteringOptions, OrderBy orderBy, Pageable pageable) {
        log.info(LogMessages.STARTED, "filterDirectors");
        Page<Director> directorPage = directorRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, EntityNames.DIRECTOR);

        Predicate<Director> directorPredicate = (director) -> ((StringUtils.isBlank(filteringOptions.getNationality()) || director.getNationality().equals(filteringOptions.getNationality()))
                && (filteringOptions.getBirthYear() == null || director.getBirthYear().getYear() == filteringOptions.getBirthYear()));

        if (filteringOptions.getLimit() == null) {
            log.info(LogMessages.PARAMETER_NULL, ParameterNames.LIMIT);
            filteringOptions.setLimit(directorRepository.count());
        }

        if (orderBy == null) {
            log.info(LogMessages.PARAMETER_NULL, ParameterNames.ORDER_BY);

            List<DirectorDto> directorDtoList = directorPage.stream()
                    .filter(directorPredicate)
                    .limit(filteringOptions.getLimit())
                    .map(directorDtoConverter::convert)
                    .toList();

            return new CustomPage<>(directorPage, directorDtoList);
        }

        Comparator<Director> directorComparator = Comparator.comparing(director -> director.getMoviesDirected().size());

        log.info(LogMessages.ORDER_BY_VALUE, orderBy.name());

        if (orderBy == OrderBy.DESC) {
            directorComparator = directorComparator.reversed();
        }

        List<DirectorDto> directorList = directorPage.stream()
                .filter(directorPredicate)
                .sorted(directorComparator)
                .limit(filteringOptions.getLimit())
                .map(directorDtoConverter::convert)
                .toList();

        return new CustomPage<>(directorPage, directorList);
    }

    @Cacheable(value = "directors", key = "#id", unless = "#result.moviesDirected.size() < 2")
    @Override
    public DirectorDto getDirector(Integer id) {
        log.info(LogMessages.STARTED, "getDirector");
        Director directorInDb = findDirectorById(id);
        log.info(LogMessages.FETCHED, EntityNames.DIRECTOR);
        return directorDtoConverter.convert(directorInDb);
    }

    @CacheEvict(value = "directors", allEntries = true)
    @Override
    public DirectorDto updateDirector(Integer id, UpdateDirectorRequest request) {
        log.info(LogMessages.STARTED, "updateDirector");

        Director directorInDb = findDirectorById(id);
        log.info(LogMessages.FETCHED, EntityNames.DIRECTOR);

        directorInDb.setName(request.getName());
        directorInDb.setSurname(request.getSurname());
        directorInDb.setNationality(request.getNationality());
        directorInDb.setBirthYear(request.getBirthYear());
        directorInDb.setBiography(request.getBiography());
        log.info(LogMessages.FIELDS_SET);

        Director savedDirector = directorRepository.save(directorInDb);
        log.info(LogMessages.SAVED, EntityNames.DIRECTOR);

        return directorDtoConverter.convert(savedDirector);
    }

    @CacheEvict(value = "directors", key = "#id")
    @Override
    public String deleteDirector(Integer id) {
        log.info(LogMessages.STARTED, "deleteDirector");

        boolean directorExists = directorRepository.existsById(id);

        if (!directorExists) {
            throw new EntityNotFound(String.format(ResponseMessages.NOT_FOUND, EntityNames.DIRECTOR, id));
        }

        log.info(LogMessages.EXISTS, EntityNames.DIRECTOR);
        directorRepository.deleteById(id);
        log.info(LogMessages.DELETED, EntityNames.DIRECTOR);

        return String.format(ResponseMessages.SUCCESS, EntityNames.DIRECTOR, id, ActionNames.DELETED);
    }

    @Cacheable(value = "directors")
    @Override
    public List<DirectorDto> getMostPopularDirectors() {
        log.info(LogMessages.STARTED, "getMostPopularDirectors");

        List<Director> directors = directorRepository.findAll();
        log.info(LogMessages.FETCHED_ALL, EntityNames.DIRECTOR);
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
        log.info(LogMessages.SAVED, EntityNames.DIRECTOR);

        return directors.stream()
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Director findDirectorById(Integer id) {
        log.info(LogMessages.STARTED, "findDirectorById");
        return directorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format(ResponseMessages.NOT_FOUND, EntityNames.DIRECTOR, id)));
    }

}
