package com.ercanbeyen.movieapplication.dto.converter;

import com.ercanbeyen.movieapplication.dto.CinemaDto;
import com.ercanbeyen.movieapplication.document.Cinema;
import org.springframework.stereotype.Component;

@Component
public class CinemaDtoConverter {
    public CinemaDto convert(Cinema cinema) {
        return CinemaDto.builder()
                .id(cinema.getId())
                .name(cinema.getName())
                .country(cinema.getCountry())
                .city(cinema.getCity())
                .address(cinema.getAddress())
                .contactNumber(cinema.getContactNumber())
                .numberOfHalls(cinema.getNumberOfHalls())
                .air_conditioning(cinema.isAir_conditioning())
                .cafe_food(cinema.isCafe_food())
                .parking_place(cinema.isParking_place())
                .threeD_animation(cinema.isThreeD_animation())
                .reservation_with_phone(cinema.isReservation_with_phone())
                .build();
    }
}
