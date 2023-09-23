package com.ercanbeyen.movieapplication.dto;

import java.util.Map;

public record Statistics<T, V>(String topic, Map<T, V> result) {

}
