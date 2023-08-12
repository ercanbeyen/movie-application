package com.ercanbeyen.movieapplication.dto;

import lombok.Data;

import java.util.Map;

@Data
public class Statistics<T, V> {
    private String topic;
    private Map<T, V> result;
}
