package com.ercanbeyen.movieapplication.dto.converter;

import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.entity.Audience;
import org.springframework.stereotype.Component;

@Component
public class AudienceDtoConverter {
    public AudienceDto convert(Audience audience) {
        return AudienceDto.builder()
                .id(audience.getId())
                .name(audience.getName())
                .surname(audience.getSurname())
                .nationality(audience.getNationality())
                .birthYear(audience.getBirthYear())
                .build();
    }
}
