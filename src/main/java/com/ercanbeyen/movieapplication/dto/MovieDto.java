package com.ercanbeyen.movieapplication.dto;

import com.ercanbeyen.movieapplication.constant.enums.Genre;
import lombok.Builder;
import lombok.Data;


import java.io.Serializable;
import java.util.Set;

@Data
@Builder
public class MovieDto implements Serializable {
    private Integer id;
    private String title;
    private String language;
    private Integer releaseYear;
    private Double rating;
    private Genre genre;
    private String summary;
    private Set<Integer> actorsIds;
    private Integer directorId;
}
