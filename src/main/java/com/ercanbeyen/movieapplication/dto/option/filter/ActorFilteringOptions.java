package com.ercanbeyen.movieapplication.dto.option.filter;

import lombok.Data;

@Data
public class ActorFilteringOptions {
    String nationality;
    Integer year;
    Integer movieId;
    Integer limit;
}
