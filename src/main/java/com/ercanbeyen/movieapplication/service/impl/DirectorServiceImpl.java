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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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


    @Override
    public DirectorDto getDirector(Integer id) {
        Director directorInDb = getDirectorById(id);

        return directorDtoConverter.convert(directorInDb);
    }

    @Override
    public DirectorDto updateDirector(Integer id, UpdateDirectorRequest request) {
        Director directorInDb = getDirectorById(id);

        directorInDb.toBuilder()
                .name(request.getName())
                .surname(request.getSurname())
                .nationality(request.getNationality())
                .birthYear(request.getBirthYear())
                .biography(request.getBiography())
                .build();

        return directorDtoConverter.convert(directorRepository.save(directorInDb));
    }

    @Override
    public String deleteDirector(Integer id) {
        directorRepository.deleteById(id);
        return "Director " + id + " is successfully deleted";
    }

    @Override
    public Director getDirectorById(Integer id) {
        return directorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Director " + id + " is not found"));
    }
}
