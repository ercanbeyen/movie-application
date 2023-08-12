package com.ercanbeyen.movieapplication.scheduler;

import com.ercanbeyen.movieapplication.constant.enums.Genre;
import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.dto.PageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Async
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    private final RestTemplate restTemplate;
    private static final String COLLECTION_URI = "http://localhost:8080/api/v1/movies";
    private static final String ID = "id";
    private static final String ELEMENT_URI = COLLECTION_URI + "/{" + ID + "}";

    @Scheduled(fixedRate = 25_000) // Every 25 seconds
    public void checkForFilterMovies() {
        final String checkedMethod = "filterMovies";
        log.info(LogMessages.TASK_STARTED, checkedMethod);

        Pageable pageable = PageRequest.of(0, 1);

        log.info(LogMessages.BEFORE_REQUEST);

        try {
            Map<String, PageDto<Movie, MovieDto>> response = restTemplate.getForObject(
                    COLLECTION_URI + "?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize(), HashMap.class
            );

            log.info(LogMessages.SUCCESS);
            log.info(LogMessages.RESPONSE_DISPLAYED, response);
        } catch (RestClientException exception) {
            log.error(LogMessages.REST_TEMPLATE_CLIENT_EXCEPTION, exception.getMessage());
        } catch (Exception exception) {
            log.error(LogMessages.UNKNOWN_EXCEPTION, exception.getMessage());
        } finally {
            log.info(LogMessages.FINALLY);
        }

        log.info(LogMessages.AFTER_REQUEST);
        log.info(LogMessages.TASK_COMPLETED, checkedMethod);
    }

    @Scheduled(fixedRate = 30_000) // Every 30 seconds
    public void checkForCreateMovie() {
        final String checkedMethod = "createMethod";
        log.info(LogMessages.STARTED, checkedMethod);

        log.info(LogMessages.BEFORE_REQUEST);

        CreateMovieRequest request = new CreateMovieRequest();
        request.setTitle("Test-movie");
        request.setGenre(Genre.ACTION);
        request.setDirectorId(null);
        request.setRating(3d);
        request.setLanguage("English");
        request.setReleaseYear(2005);

        try {
            HashMap<String, MovieDto> response = restTemplate.postForObject(COLLECTION_URI, request, HashMap.class);
            log.info(LogMessages.SUCCESS);
            log.info(LogMessages.RESPONSE_DISPLAYED, response);
        } catch (RestClientException exception) {
            log.error(LogMessages.REST_TEMPLATE_CLIENT_EXCEPTION, exception.getMessage());
        } catch (Exception exception) {
            log.error(LogMessages.UNKNOWN_EXCEPTION, exception.getMessage());
        } finally {
            log.info(LogMessages.FINALLY);
        }

        log.info(LogMessages.AFTER_REQUEST);
        log.info(LogMessages.TASK_COMPLETED, checkedMethod);
    }

    @Scheduled(cron = "0/15 * * * * *") // Every 15 seconds
    public void checkForGetMovie() {
        final String checkedMethod = "getMovie";
        log.info(LogMessages.TASK_STARTED, checkedMethod);

        Map<String, Integer> parameters = new HashMap<>();
        parameters.put(ID, 1);

        log.info(LogMessages.BEFORE_REQUEST);

        try {
            Map<String, MovieDto> response = restTemplate.getForObject(ELEMENT_URI, HashMap.class, parameters);
            log.info(LogMessages.SUCCESS);
            log.info(LogMessages.RESPONSE_DISPLAYED, response);
        } catch (RestClientException exception) {
            log.error(LogMessages.REST_TEMPLATE_CLIENT_EXCEPTION, exception.getMessage());
        } catch (Exception exception) {
            log.error(LogMessages.UNKNOWN_EXCEPTION, exception.getMessage());
        } finally {
            log.info(LogMessages.FINALLY);
        }

        log.info(LogMessages.AFTER_REQUEST);
        log.info(LogMessages.TASK_COMPLETED, checkedMethod);
    }

    @Scheduled(cron = "0 * * * * *") // Every minute
    public void checkForUpdateMovie() {
        final String checkedMethod = "updateMovie";
        log.info(LogMessages.TASK_STARTED, checkedMethod);

        Map<String, Integer> parameters = new HashMap<>();
        parameters.put(ID, 1);

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setTitle("Test-movie");
        request.setGenre(Genre.ACTION);
        request.setRating(3d);
        request.setLanguage("English");
        request.setReleaseYear(2005);

        log.info(LogMessages.BEFORE_REQUEST);

        try {
            restTemplate.put(ELEMENT_URI, request, parameters);
            log.info(LogMessages.SUCCESS);
        } catch (RestClientException exception) {
            log.error(LogMessages.REST_TEMPLATE_CLIENT_EXCEPTION, exception.getMessage());
        } catch (Exception exception) {
            log.error(LogMessages.UNKNOWN_EXCEPTION, exception.getMessage());
        } finally {
            log.info(LogMessages.FINALLY);
        }

        log.info(LogMessages.AFTER_REQUEST);
        log.info(LogMessages.TASK_COMPLETED, checkedMethod);
    }

    @Scheduled(cron = "0 0/2 * * * *") // Every 2 minutes
    public void checkForDelete() {
        final String checkedMethod = "deleteMovie";
        log.info(LogMessages.TASK_STARTED, checkedMethod);

        Map<String, Integer> parameters = new HashMap<>();
        parameters.put(ID, 1);

        log.info(LogMessages.BEFORE_REQUEST);

        try {
            restTemplate.delete(ELEMENT_URI, parameters);
            log.info(LogMessages.SUCCESS);
        } catch (RestClientException exception) {
            log.error(LogMessages.REST_TEMPLATE_CLIENT_EXCEPTION, exception.getMessage());
        } catch (Exception exception) {
            log.error(LogMessages.UNKNOWN_EXCEPTION, exception.getMessage());
        } finally {
            log.info(LogMessages.FINALLY);
        }

        log.info(LogMessages.AFTER_REQUEST);
        log.info(LogMessages.TASK_COMPLETED, checkedMethod);
    }

}
