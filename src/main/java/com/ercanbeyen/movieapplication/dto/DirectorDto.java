package com.ercanbeyen.movieapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class DirectorDto implements Serializable {
    private Integer id;
    private String name;
    private String surname;
    private String nationality;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthYear;
    private String biography;
    private List<Integer> moviesDirected;
}
