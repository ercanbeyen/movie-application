package com.ercanbeyen.movieapplication.scheduler;

import com.ercanbeyen.movieapplication.constant.enums.Genre;
import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component
@Async
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"unchecked"})
public class ScheduledTasks {
    private final RestTemplate restTemplate;
    private static final String COLLECTION_URI = "http://localhost:8080/api/v1/movies";
    private static final String ID = "id";
    private static final String ELEMENT_URI = COLLECTION_URI + "/{" + ID + "}";

    @Scheduled(fixedRate = 150_000) // Every 150 seconds
    public void checkForFilterMovies() {
        final String checkedMethod = "filterMovies";
        log.info(LogMessages.TASK_STARTED, checkedMethod);

        Pageable pageable = PageRequest.of(0, 1);

        log.info(LogMessages.BEFORE_REQUEST);

        try {
            UriComponents uriComponents = UriComponentsBuilder
                    .fromUriString(COLLECTION_URI)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .build();

            Map<String, PageDto<Movie, MovieDto>> response = restTemplate.getForObject(
                    uriComponents.toUriString(), HashMap.class
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

    @Scheduled(cron = "0 0 9 1 * *") // 9:00 PM on the first day of every month
    public void checkForCreateMovie() {
        final String checkedMethod = "createMethod";
        log.info(LogMessages.STARTED, checkedMethod);

        log.info(LogMessages.BEFORE_REQUEST);

        CreateMovieRequest request = new CreateMovieRequest();
        request.setImdbId("tt2964642");
        request.setTitle("Test-movie");
        request.setGenre(Genre.ACTION);
        request.setDirectorId(null);
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

    @Scheduled(cron = "0 0 18 * * MON-FRI") // Every weekday at 6:00 PM
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

    @Scheduled(cron = "0 0 8-10 * * *") // 8, 9 and 10 o'clock of every day
    public void checkForUpdateMovie() {
        final String checkedMethod = "updateMovie";
        log.info(LogMessages.TASK_STARTED, checkedMethod);

        Map<String, Integer> parameters = new HashMap<>();
        parameters.put(ID, 1);

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setTitle("Test-movie");
        request.setImdbId("tt2964642");
        request.setGenre(Genre.ACTION);
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

    @Scheduled(cron = "0 0 */8 * * *") // Every 8 hours on the hour
    public void checkForDeleteMovie() {
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

    @Scheduled(cron = "0 0 0 25 12 ?") // Every Christmas Day
    public void checkForCalculateStatistics() {
        final String checkedMethod = "calculateStatistics";
        log.info(LogMessages.TASK_STARTED, checkedMethod);

        log.info(LogMessages.BEFORE_REQUEST);

        try {
            Map<String, Statistics<String, String>> response = restTemplate.getForObject(
                    COLLECTION_URI + "/statistics", HashMap.class
            );
            log.info(LogMessages.SUCCESS);
            log.info(LogMessages.RESPONSE_DISPLAYED, response);
        } catch (Exception exception) {
            log.error(LogMessages.UNKNOWN_EXCEPTION, exception.getMessage());
        } finally {
            log.info(LogMessages.FINALLY);
        }

        log.info(LogMessages.AFTER_REQUEST);
        log.info(LogMessages.TASK_COMPLETED, checkedMethod);
    }

}
