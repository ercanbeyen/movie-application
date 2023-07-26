package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.OrderBy;
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
        Director newDirector = Director.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .moviesDirected(new ArrayList<>())
                .build();

        return directorDtoConverter.convert(directorRepository.save(newDirector));
    }

    @Override
    public List<DirectorDto> getDirectors(DirectorFilteringOptions filteringOptions, OrderBy orderBy) {
        List<Director> directors = directorRepository.findAll();

        if (!StringUtils.isBlank(filteringOptions.getNationality())) {
            directors = directors.stream()
                    .filter(director -> director.getNationality().equals(filteringOptions.getNationality()))
                    .collect(Collectors.toList());
            log.info("Directors are filtered by nationality");
        }

        if (filteringOptions.getYear() != null) {
            directors = directors.stream()
                    .filter(director -> director.getBirthYear().getYear() == filteringOptions.getYear())
                    .collect(Collectors.toList());
            log.info("Directors are filtered by year");
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

            log.info("Directors are sorted by number of movies directed");

            if (filteringOptions.getLimit() != null) {
                directors = directors.stream()
                        .limit(filteringOptions.getLimit())
                        .toList();
                log.info("Top {} directors are selected", filteringOptions.getLimit());
            }
        }

        return directors.stream()
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "directors", key = "#id", unless = "#result.moviesDirected.size() < 2")
    @Override
    public DirectorDto getDirector(Integer id) {
        log.info("Fetch director from database");
        Director directorInDb = getDirectorById(id);
        return directorDtoConverter.convert(directorInDb);
    }

    @CacheEvict(value = "directors", allEntries = true)
    @Override
    public DirectorDto updateDirector(Integer id, UpdateDirectorRequest request) {
        log.info("Update director operation is starting");
        Director directorInDb = getDirectorById(id);

        directorInDb.setName(request.getName());
        directorInDb.setSurname(request.getSurname());
        directorInDb.setNationality(request.getNationality());
        directorInDb.setBirthYear(request.getBirthYear());
        directorInDb.setBiography(request.getBiography());

        return directorDtoConverter.convert(directorRepository.save(directorInDb));
    }

    @CacheEvict(value = "directors", key = "#id")
    @Override
    public String deleteDirector(Integer id) {
        log.info("Delete director operation is starting");
        directorRepository.deleteById(id);
        return "Director " + id + " is successfully deleted";
    }

    @Cacheable(value = "directors")
    @Override
    public List<DirectorDto> getMostPopularDirector() {
        log.info("Fetch directors from database");
        List<Director> directors = directorRepository.findAll();
        int numberOfMovies = 2;

        return directors.stream()
                .filter(director -> director.getMoviesDirected().size() >= numberOfMovies)
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<DirectorDto> searchDirectors(String fullName) {
        List<Director> directors = directorRepository.findByFullName(fullName);
        return directors.stream()
                .map(directorDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Director getDirectorById(Integer id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Director " + id + " is not found"));
    }

    @Override
    public CustomPage<DirectorDto, Director> getDirectors(Pageable pageable) {
        Page<Director> page = directorRepository.findAll(pageable);
        List<DirectorDto> directors = page.getContent().stream()
                .map(directorDtoConverter::convert)
                .toList();

        return new CustomPage<>(page, directors);
    }
}
