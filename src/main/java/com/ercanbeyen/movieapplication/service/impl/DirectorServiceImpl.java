package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.dto.DirectorDto;
import com.ercanbeyen.movieapplication.dto.converter.DirectorDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateDirectorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateDirectorRequest;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.exception.EntityNotFound;
import com.ercanbeyen.movieapplication.repository.DirectorRepository;
import com.ercanbeyen.movieapplication.service.DirectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    public List<DirectorDto> getDirectors(String nationality, Integer year) {
        List<Director> directors = directorRepository.findAll();

        if (!StringUtils.isBlank(nationality)) {
            directors = directors.stream()
                    .filter(director -> director.getNationality().equals(nationality))
                    .collect(Collectors.toList());
        }

        if (year != null) {
            directors = directors.stream()
                    .filter(director -> director.getBirthYear().getYear() == year)
                    .collect(Collectors.toList());
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
}
