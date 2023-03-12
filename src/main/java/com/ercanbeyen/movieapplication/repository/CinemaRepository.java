package com.ercanbeyen.movieapplication.repository;

import com.ercanbeyen.movieapplication.document.Cinema;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CinemaRepository extends ElasticsearchRepository<Cinema, String> {
     SearchHits<Cinema> findByName(String searchTerm);
     @Query("{\"bool\": {" +
             "\"must\": [" +
             "{\"match\": {\"reservation_with_phone\": \"?0\"}}," +
             "{\"match\": {\"threeD_animation\": \"?1\"}}," +
             "{\"match\": {\"parking_place\": \"?2\"}}," +
             "{\"match\": {\"air_conditioning\": \"?3\"}}," +
             "{\"match\": {\"cafe_food\": \"?4\"}}]}}")
     List<Cinema> findByStatuses(boolean reservation_with_phone, boolean threeD_animation,
             boolean parking_place, boolean air_conditioning, boolean cafe_food);
}
