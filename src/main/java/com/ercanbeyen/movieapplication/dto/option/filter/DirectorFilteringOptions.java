package com.ercanbeyen.movieapplication.dto.option.filter;

import com.ercanbeyen.movieapplication.constant.OrderBy;
import lombok.Data;

@Data
public class DirectorFilteringOptions {
    String nationality;
    Integer year;
    Integer limit;
}
