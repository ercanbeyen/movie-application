package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.document.Cinema;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CinemaRepository extends ElasticsearchRepository<Cinema, String> {

}
