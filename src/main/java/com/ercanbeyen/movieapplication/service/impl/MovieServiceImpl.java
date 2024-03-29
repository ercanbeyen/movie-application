package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.constant.message.StatisticsMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.RatingDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.*;
import com.ercanbeyen.movieapplication.exception.ResourceConflictException;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.repository.MovieRepository;
import com.ercanbeyen.movieapplication.service.*;
import com.ercanbeyen.movieapplication.util.StatisticsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieDtoConverter movieDtoConverter;
    private final DirectorService directorService;
    private final ActorService actorService;
    private final AudienceService audienceService;
    private final RatingService ratingService;

    @CachePut(value = "movies", key = "#result.id")
    @Override
    public MovieDto createMovie(CreateMovieRequest request) {
        checkImdbId(null, request.getImdbId());

        Movie newMovie = Movie.builder()
                .imdbId(request.getImdbId())
                .title(request.getTitle())
                .genre(request.getGenre())
                .averageRating(0d)
                .releaseYear(request.getReleaseYear())
                .language(request.getLanguage())
                .summary(request.getSummary())
                .director(null)
                .actors(new HashSet<>())
                .build();

        Movie createdMovie = movieRepository.save(newMovie);
        log.info(LogMessages.SAVED, ResourceNames.MOVIE);

        return movieDtoConverter.convert(createdMovie);
    }

    @CacheEvict(value = "movies", allEntries = true)
    @Override
    public PageDto<Movie, MovieDto> getMovies(MovieFilteringOptions filteringOptions, String limit, Pageable pageable) {
        Predicate<Movie> moviePredicate = (movie) -> (
                (filteringOptions.genres() == null || filteringOptions.genres().isEmpty() || filteringOptions.genres().contains(movie.getGenre())) &&
                (StringUtils.isBlank(filteringOptions.language()) || movie.getLanguage().equals(filteringOptions.language())) &&
                (filteringOptions.releaseYear() == null || movie.getReleaseYear().intValue() == filteringOptions.releaseYear().intValue()));

        Page<Movie> moviePage = movieRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.MOVIE);
        long maximumSize = Long.parseLong(limit);

        List<MovieDto> movieDtoList = moviePage.stream()
                 .filter(moviePredicate)
                 .limit(maximumSize)
                 .map(movieDtoConverter::convert)
                 .toList();

        return new PageDto<>(moviePage, movieDtoList);
    }

    @Cacheable(value = "movies", key = "#id", unless = "#result.releaseYear < 2020")
    @Override
    public MovieDto getMovie(Integer id) {
        Movie movieInDb = findMovieById(id);
        return movieDtoConverter.convert(movieInDb);
    }

    @CacheEvict(value = "movies", allEntries = true)
    @Transactional
    @Override
    public MovieDto updateMovie(Integer id, UpdateMovieRequest request) {
        Movie movieInDb = findMovieById(id);

        checkImdbId(movieInDb.getImdbId(), request.getImdbId());
        movieInDb.setImdbId(request.getImdbId());

        if (request.getDirectorId() != null) {
            Director director = directorService.findDirector(request.getDirectorId());
            movieInDb.setDirector(director);
            log.info(LogMessages.RESOURCE_FOUND, ResourceNames.DIRECTOR, id);
        } else {
            log.warn(LogMessages.SEARCH_SKIPPED, ResourceNames.DIRECTOR);
        }

        movieInDb.getActors().clear();

        if (request.getActorIds() != null) {
            Set<Actor> actorSet = new HashSet<>();
            for (Integer actorId : request.getActorIds()) {
                Actor actorInDb = actorService.findActor(actorId);
                actorInDb.getMoviesPlayed().add(movieInDb);
                actorSet.add(actorInDb);
                log.info(LogMessages.RESOURCE_FOUND, ResourceNames.ACTOR, id);
            }

            movieInDb.setActors(actorSet);
        } else {
            log.warn(LogMessages.SEARCH_SKIPPED, ResourceNames.ACTOR);
        }

        movieInDb.setTitle(request.getTitle());
        movieInDb.setGenre(request.getGenre());
        movieInDb.setLanguage(request.getLanguage());
        movieInDb.setReleaseYear(request.getReleaseYear());
        movieInDb.setSummary(request.getSummary());
        log.info(LogMessages.FIELDS_SET);

        Movie savedMovie = movieRepository.save(movieInDb);
        log.info(LogMessages.SAVED, ResourceNames.MOVIE);

        return movieDtoConverter.convert(savedMovie);
    }

    @CacheEvict(value = "movies", key = "#id")
    @Transactional
    @Override
    public String deleteMovie(Integer id) {
        movieRepository.findById(id)
                .ifPresentOrElse(movieRepository::delete, () -> {
                    throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE));
                });

        log.info(LogMessages.DELETED, ResourceNames.MOVIE);
        return ResponseMessages.SUCCESS;
    }

    @Cacheable(value = "movies")
    @Override
    public List<MovieDto> getLatestMovies() {
        List<Movie> movies = movieRepository.findAll();
        log.info(LogMessages.FETCHED_ALL, ResourceNames.MOVIE);
        int year = 2020;

        return movies.stream()
                .filter(movie -> movie.getReleaseYear() >= year)
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDto> searchMovies(String title) {
        List<Movie> movies = movieRepository.findByTitleStartingWith(title);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.MOVIE);


        return movies.stream()
                .map(movieDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public MovieDto getMovie(String imdbId) {
        Movie movie = movieRepository.findByImdbId(imdbId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE)));

        return movieDtoConverter.convert(movie);
    }

    @CacheEvict(value = "movies", key = "#id")
    @Transactional
    @Override
    public MovieDto rateMovie(Integer id, Double rate, UserDetails userDetails) {
        Movie movie = findMovieById(id);
        CompletableFuture<Audience> audienceFuture = audienceService.findAudienceAsync(userDetails.getUsername());

        return findRatingByMovieAndAudience.andThen(optionalRating -> {
            boolean isRatingPresent = optionalRating.isPresent();
            String logMessage = isRatingPresent ? ResourceNames.RATING + " is created before"
                    : ResourceNames.RATING + " has not been created before";
            log.info(logMessage);

            RatingDto ratingDto = (isRatingPresent) ? ratingService.updatedRating(optionalRating.get(), rate)
                    : ratingService.createRating(audienceFuture.join(), movie, rate);

            if (ratingDto == null) {
                throw new IllegalStateException("Unable to rate " + ResourceNames.MOVIE + " " + movie.getId());
            }

            Movie savedMovie = updateRatingOfMovie(movie);
            return movieDtoConverter.convert(savedMovie);
        }).apply(movie, audienceFuture);
    }

    public Movie updateRatingOfMovie(Movie movie) {
        Double averageRating = calculateAverageRating.apply(movie);
        movie.setAverageRating(averageRating);
        log.info(LogMessages.FIELDS_SET);

        Movie savedMovie = movieRepository.save(movie);
        log.info(LogMessages.SAVED, ResourceNames.MOVIE);

        return savedMovie;
    }

    @Override
    public MovieDto deleteRatingOfMovie(Integer id, Integer audienceId) {
        Movie movieInDb = findMovieById(id);

        ratingService.deleteRating(id, audienceId);
        Double averageRating = calculateAverageRating.apply(movieInDb);
        movieInDb.setAverageRating(averageRating);
        log.info(LogMessages.FIELDS_SET);

        movieRepository.save(movieInDb);
        log.info(LogMessages.SAVED, ResourceNames.RATING);

        return movieDtoConverter.convert(movieInDb);
    }

    @Override
    public Statistics<String, String> calculateStatistics() {
        Map<String, String> statisticsMap = new HashMap<>();
        List<Movie> movieList = movieRepository.findAll();

        Comparator<Movie> movieComparator = Comparator.comparing(Movie::getAverageRating);

        String titleOfMostRatedMovie = movieList.stream()
                .max(movieComparator)
                .map(Movie::getTitle)
                .orElse(StatisticsMessages.NOT_EXISTS);

        statisticsMap.put("mostRatedMovie", titleOfMostRatedMovie);

        String titleOfLeastRatedMovie = movieList.stream()
                .min(movieComparator)
                .map(Movie::getTitle)
                .orElse(StatisticsMessages.NOT_EXISTS);

        statisticsMap.put("leastRatedMovie", titleOfLeastRatedMovie);

        List<String> languageList = movieList.stream()
                .map(Movie::getLanguage)
                .toList();

        String mostPopularLanguage = StatisticsUtil.calculateMostOccurred(languageList);
        mostPopularLanguage = StatisticsUtil.valueAssignmentToStringItem(mostPopularLanguage);
        statisticsMap.put("mostPopularLanguage", mostPopularLanguage);

        String leastPopularLanguage = StatisticsUtil.calculateLeastOccurred(languageList);
        leastPopularLanguage = StatisticsUtil.valueAssignmentToStringItem(leastPopularLanguage);
        statisticsMap.put("leastPopularLanguage", leastPopularLanguage);

        return new Statistics<>(ResourceNames.MOVIE, statisticsMap);
    }

    private void checkImdbId(String previousImdbId, String newImdbId) {
        if (StringUtils.isNotBlank(previousImdbId) && newImdbId.equals(previousImdbId)) {
            log.warn("Same imdbId is going to be assigned.");
            return;
        }

        if (movieRepository.existsByImdbId(newImdbId)) {
            throw new ResourceConflictException(String.format(ResponseMessages.ALREADY_EXISTS, ResourceNames.MOVIE));
        }

        log.info("imdbId check is passed");
    }

    private static final BiFunction<Movie, CompletableFuture<Audience>, Optional<Rating>> findRatingByMovieAndAudience = (movie, audienceFuture) -> movie.getRatings()
            .stream()
            .filter(rating -> rating.getAudience().getId().intValue() == audienceFuture.join().getId().intValue() &&
                    rating.getMovie().getId().intValue() == movie.getId().intValue())
            .findFirst();

    private Movie findMovieById(Integer id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE)));
    }

    private final Function<Movie, Double> calculateAverageRating = movie -> movie.getRatings()
                .stream()
                .mapToDouble(Rating::getRate)
                .average()
                .orElse(0);
}
