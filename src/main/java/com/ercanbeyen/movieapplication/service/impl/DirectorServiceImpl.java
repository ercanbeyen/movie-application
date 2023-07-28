package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.constant.message.ActionNames;
import com.ercanbeyen.movieapplication.constant.message.EntityNames;
import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private final DirectorDtoConverter directorDtoConverter;

    @Override
    public DirectorDto createDirector(CreateDirectorRequest request) {
        log.info(String.format(LogMessages.STARTED, "createDirector"));

        Director newDirector = Director.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .moviesDirected(new ArrayList<>())
                .build();

        Director savedDirector = directorRepository.save(newDirector);
        log.info(String.format(LogMessages.SAVED, EntityNames.DIRECTOR));

        return directorDtoConverter.convert(savedDirector);
    }

    @Override
    public List<DirectorDto> getDirectors(DirectorFilteringOptions filteringOptions, OrderBy orderBy) {
        log.info(String.format(LogMessages.STARTED, "getDirectors"));
        List<Director> directors = directorRepository.findAll();
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.DIRECTOR));

        if (!StringUtils.isBlank(filteringOptions.getNationality())) {
            directors = directors.stream()
                    .filter(director -> director.getNationality().equals(filteringOptions.getNationality()))
                    .collect(Collectors.toList());
            log.info(String.format(LogMessages.FILTERED, "nationality"));
        }

        if (filteringOptions.getYear() != null) {
            directors = directors.stream()
                    .filter(director -> director.getBirthYear().getYear() == filteringOptions.getYear())
                    .collect(Collectors.toList());
            log.info(String.format(LogMessages.FILTERED, "birthYear"));
        }

        if (orderBy != null) {
            directors = directors.stream()
                    .sorted((director1, director2) -> {
                        int numberOfMoviesDirected1 = director1.getMoviesDirected().size();
                        int numberOfMoviesDirected2 = director2.getMoviesDirected().size();

                        if (orderBy == OrderBy.DESC) {
                            return Integer.compare(numberOfMoviesDirected2, numberOfMoviesDirected1);
                        } else {
                            return Integer.compare(numberOfMoviesDirected1, numberOfMoviesDirected2);
                        }
                    })
                    .toList();

            log.info(String.format(LogMessages.SORTED, "number of movies directed"));

            if (filteringOptions.getLimit() != null) {
                directors = directors.stream()
                        .limit(filteringOptions.getLimit())
                        .toList();
                log.info(String.format(LogMessages.LIMITED, filteringOptions.getLimit()));
            }
        }

        return directors.stream()
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "directors", key = "#id", unless = "#result.moviesDirected.size() < 2")
    @Override
    public DirectorDto getDirector(Integer id) {
        log.info(String.format(LogMessages.STARTED, "getDirector"));
        Director directorInDb = getDirectorById(id);
        log.info(String.format(LogMessages.FETCHED, EntityNames.DIRECTOR));
        return directorDtoConverter.convert(directorInDb);
    }

    @CacheEvict(value = "directors", allEntries = true)
    @Override
    public DirectorDto updateDirector(Integer id, UpdateDirectorRequest request) {
        log.info(String.format(LogMessages.STARTED, "updateDirector"));

        Director directorInDb = getDirectorById(id);
        log.info(String.format(LogMessages.FETCHED, EntityNames.DIRECTOR));

        directorInDb.setName(request.getName());
        directorInDb.setSurname(request.getSurname());
        directorInDb.setNationality(request.getNationality());
        directorInDb.setBirthYear(request.getBirthYear());
        directorInDb.setBiography(request.getBiography());
        log.info(LogMessages.FIELDS_SET);

        Director savedDirector = directorRepository.save(directorInDb);
        log.info(String.format(LogMessages.SAVED, EntityNames.DIRECTOR));

        return directorDtoConverter.convert(savedDirector);
    }

    @CacheEvict(value = "directors", key = "#id")
    @Override
    public String deleteDirector(Integer id) {
        log.info(String.format(LogMessages.STARTED, "deleteDirector"));

        boolean directorExists = directorRepository.existsById(id);

        if (!directorExists) {
            throw new EntityNotFound(String.format(ResponseMessages.NOT_FOUND, EntityNames.DIRECTOR, id));
        }

        log.info(String.format(LogMessages.EXISTS, EntityNames.DIRECTOR));
        directorRepository.deleteById(id);
        log.info(String.format(LogMessages.DELETED, EntityNames.DIRECTOR));

        return String.format(ResponseMessages.SUCCESS, EntityNames.DIRECTOR, id, ActionNames.DELETED);
    }

    @Cacheable(value = "directors")
    @Override
    public List<DirectorDto> getMostPopularDirectors() {
        log.info(String.format(LogMessages.STARTED, "getMostPopularDirectors"));

        List<Director> directors = directorRepository.findAll();
        log.info(String.format(LogMessages.FETCHED_ALL, EntityNames.DIRECTOR));
        int numberOfMovies = 2;

        return directors.stream()
                .filter(director -> director.getMoviesDirected().size() >= numberOfMovies)
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<DirectorDto> searchDirectors(String fullName) {
        log.info(String.format(LogMessages.STARTED, "searchDirectors"));

        List<Director> directors = directorRepository.findByFullName(fullName);
        log.info(String.format(LogMessages.SAVED, EntityNames.DIRECTOR));

        return directors.stream()
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Director getDirectorById(Integer id) {
        log.info(String.format(LogMessages.STARTED, "getDirectorById"));
        return directorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format(ResponseMessages.NOT_FOUND, EntityNames.DIRECTOR, id)));
    }

    @Override
    public CustomPage<DirectorDto, Director> getDirectors(Pageable pageable) {
        log.info(String.format(LogMessages.STARTED, "getDirectors"));

        Page<Director> page = directorRepository.findAll(pageable);
        List<DirectorDto> directors = page.getContent().stream()
                .map(directorDtoConverter::convert)
                .toList();

        return new CustomPage<>(page, directors);
    }
}
