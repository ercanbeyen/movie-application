package com.ercanbeyen.movieapplication.dto.option.filter;

import lombok.Data;

@Data
public class ActorFilteringOptions {
    String nationality;
    Integer birthYear;
    Integer movieId;
    Long limit;
}
