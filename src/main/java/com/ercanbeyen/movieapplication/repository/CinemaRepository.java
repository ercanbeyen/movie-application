package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.document.Cinema;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CinemaRepository extends ElasticsearchRepository<Cinema, String> {
     SearchHits<Cinema> searchByName(String name);
}
