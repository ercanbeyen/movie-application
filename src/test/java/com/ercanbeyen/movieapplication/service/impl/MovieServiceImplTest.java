package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.defaults.DefaultValues;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.Statistics;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.constant.enums.Genre;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.MovieRepository;
import com.ercanbeyen.movieapplication.dto.PageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {
    @InjectMocks
    private MovieServiceImpl movieService;
    @Mock
    private MovieRepository movieRepository;
    @Spy
    private MovieDtoConverter movieDtoConverter;
    @Mock
    private DirectorServiceImpl directorService;
    @Mock
    private ActorServiceImpl actorService;
    private List<Movie> movieList;
    private List<MovieDto> movieDtoList;

    private Director getMockDirector() {
        return Director.builder()
                .id(1)
                .name("Test-name")
                .surname("Test-surname")
                .birthDate(LocalDate.of(2012, 5, 12))
                .nationality("Test-nationality")
                .biography("Test-biography")
                .moviesDirected(new ArrayList<>())
                .build();
    }

    private List<Actor> getMockActors() {
        Actor actor = Actor.builder()
                .id(1)
                .name("Test-name")
                .surname("Test-surname")
                .birthDate(LocalDate.of(2005, 3, 9))
                .nationality("Test-nationality")
                .biography("Test-biography")
                .moviesPlayed(new HashSet<>())
                .build();

        return Collections.singletonList(actor);
    }

    private Set<Integer> getMockActorIds() {
        List<Actor> actorList = getMockActors();

        return actorList.stream()
                .map(Actor::getId)
                .collect(Collectors.toSet());
    }

    private List<Movie> getMockMovieList() {
        int id = 1;
        String title = "Test-title";
        Genre genre = Genre.SCIENCE_FICTION;
        String summary = "Test-summary";
        Director director = getMockDirector();
        Set<Actor> actors = new HashSet<>(getMockActors());

        Movie movie1 = Movie.builder()
                .id(id)
                .imdbId("tt2964641")
                .title(title)
                .genre(genre)
                .rating(3.4)
                .releaseYear(2022)
                .language("English")
                .summary(summary)
                .director(director)
                .actors(actors)
                .build();

        id++;

        Movie movie2 = Movie.builder()
                .id(id)
                .imdbId("tt2964642")
                .title(title)
                .genre(genre)
                .rating(2.7)
                .releaseYear(2015)
                .language("Spanish")
                .summary(summary)
                .director(director)
                .actors(actors)
                .build();

        return Arrays.asList(movie1, movie2);
    }

    @BeforeEach
    public void setup() {
        movieList = getMockMovieList();
        MovieDtoConverter setupMovieDtoConverter = new MovieDtoConverter();
        movieDtoList = movieList.stream()
                .map(setupMovieDtoConverter::convert)
                .toList();
    }

    @Test
    @DisplayName("When createMovie Called With Valid Request It Should Return MovieDto")
    public void whenCreateMovieCalledWithValidRequest_itShouldReturnMovieDto() {
        Movie movie = movieList.get(0);
        MovieDto expected = movieDtoList.get(0);
        int directorId = expected.directorId();

        CreateMovieRequest request = new CreateMovieRequest();
        request.setTitle(movie.getTitle());
        request.setGenre(movie.getGenre());
        request.setReleaseYear(movie.getReleaseYear());
        request.setLanguage(movie.getLanguage());
        request.setDirectorId(expected.directorId());
        request.setRating(expected.rating());
        request.setSummary(movie.getSummary());

        when(directorService.findDirectorById(directorId)).thenReturn(movie.getDirector());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieDtoConverter.convert(movie)).thenReturn(expected);

        MovieDto actual = movieService.createMovie(request);

        assertEquals(expected, actual);

        verify(directorService, times(1)).findDirectorById(directorId);
        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }


    @Test
    @DisplayName("When getMovie Called With Existed Id It Should Return MovieDto")
    public void whenGetMovieCalledExistedId_itShouldReturnMovieDto() {
        Movie movie = movieList.get(0);
        int id = movie.getId();

        MovieDto expected = movieDtoList.get(0);
        Optional<Movie> optionalMovie = Optional.of(movie);

        when(movieRepository.findById(id)).thenReturn(optionalMovie);
        when(movieDtoConverter.convert(movie)).thenReturn(expected);

        MovieDto actual = movieService.getMovie(id);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findById(id);
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When getMovie Called With Not Existed Id It Should Throw ResourceNotFoundException")
    public void whenGetMovieCalledWithNotExistedId_itShouldThrowResourceNotFoundException() {
        int id = 15;
        Optional<Movie> movieOptional = Optional.empty();

        when(movieRepository.findById(id)).thenReturn(movieOptional);

        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.getMovie(id));
        String expected = exception.getMessage();

        String actual = String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findById(id);
        verifyNoMoreInteractions(movieRepository);
        verifyNoInteractions(movieDtoConverter);
    }

    @Test
    @DisplayName("When getMovie Called With Existed Imdb Id It Should Return MovieDto")
    public void whenGetMovieCalledWithExistedImdbId_itShouldReturnMovieDto() {
        Movie movie = movieList.get(0);
        String imdbId = movie.getImdbId();

        MovieDto expected = movieDtoList.get(0);
        Optional<Movie> optionalMovie = Optional.of(movie);

        when(movieRepository.findByImdbId(imdbId)).thenReturn(optionalMovie);
        when(movieDtoConverter.convert(movie)).thenReturn(expected);

        MovieDto actual = movieService.getMovie(imdbId);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findByImdbId(imdbId);
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When getMovies Called With Parameters It Should Return MovieDto List")
    public void whenFilterMoviesCalledWithParameters_itShouldReturnMovieDto() {
        Pageable pageable = Pageable.ofSize(1).withPage(0);

        List<Movie> fetchedMovieList = Collections.singletonList(movieList.get(0));
        List<MovieDto> fetchedMovieDtoList = Collections.singletonList(movieDtoList.get(0));

        Page<Movie> moviePage = new PageImpl<>(fetchedMovieList, pageable, fetchedMovieList.size());
        PageDto<Movie, MovieDto> expected = new PageDto<>(moviePage, fetchedMovieDtoList);

        when(movieRepository.findAll(pageable)).thenReturn(moviePage);
        when(movieDtoConverter.convert(movieList.get(0))).thenReturn(movieDtoList.get(0));

        MovieFilteringOptions movieFilteringOptions = new MovieFilteringOptions(movieList.get(0).getLanguage(), null, null);

        PageDto<Movie, MovieDto> actual = movieService.getMovies(movieFilteringOptions, null, DefaultValues.DEFAULT_LIMIT_VALUE, pageable);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findAll(pageable);
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When updateMovie Called With Existed Id And Valid Request With Null Director Id and Null Actor Id Set It Should Return MovieDto")
    public void whenUpdateMovieCalledWithExistedIdAndValidRequestWithNullDirectorIdAndNullActorIdSet_itShouldReturnMovieDto() {
        Movie movie = movieList.get(0);
        Movie updatedMovie = movieList.get(1);
        updatedMovie.setDirector(null);
        updatedMovie.setActors(new HashSet<>());
        MovieDtoConverter internalDtoConverter = new MovieDtoConverter();
        MovieDto expected = internalDtoConverter.convert(updatedMovie);
        int id = movie.getId();

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setImdbId(expected.imdbId());
        request.setTitle(expected.title());
        request.setGenre(expected.genre());
        request.setReleaseYear(expected.releaseYear());
        request.setLanguage(expected.language());
        request.setRating(expected.rating());
        request.setSummary(expected.summary());
        request.setDirectorId(expected.directorId());
        request.setActorIds(null);

        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieDtoConverter.convert(movie)).thenReturn(expected);

        MovieDto actual = movieService.updateMovie(id, request);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findById(id);
        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When Update Movie Called With Existed Id And Valid Request With Non Null DirectorId And Non Null Actor Id It Should Return MovieDto")
    public void whenUpdateMovieCalledWithExistedIdAndValidRequestNonNullDirectorIdAndNonNullActorId_itShouldReturnMovieDto() {
        Movie movie = movieList.get(0);
        Movie updatedMovie = movieList.get(1);
        MovieDto expected = movieDtoList.get(1);

        int id = movie.getId();
        Director director = getMockDirector();
        Actor actor = getMockActors().get(0);

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setImdbId(expected.imdbId());
        request.setTitle(expected.title());
        request.setGenre(expected.genre());
        request.setReleaseYear(expected.releaseYear());
        request.setLanguage(expected.language());
        request.setRating(expected.rating());
        request.setSummary(expected.summary());
        request.setDirectorId(getMockDirector().getId());
        request.setActorIds(getMockActorIds());

        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));
        when(directorService.findDirectorById(director.getId())).thenReturn(director);
        when(actorService.findActorById(actor.getId())).thenReturn(actor);
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);
        when(movieDtoConverter.convert(updatedMovie)).thenReturn(expected);

        MovieDto actual = movieService.updateMovie(id, request);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findById(id);
        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When updateMovie Called With Not Existed Id It Should Throw ResourceNotFoundException")
    public void whenUpdateMovieCalledWithNotExistedId_itShouldThrowResourceNotFoundException() {
        int id = 15;
        Optional<Movie> movieOptional = Optional.empty();

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setTitle("Test-title");
        request.setLanguage("Test-language");
        request.setRating(2d);
        request.setSummary("Test-summary");

        String expected = String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE);

        when(movieRepository.findById(id)).thenReturn(movieOptional);

        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(id, request));
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findById(id);
        verifyNoMoreInteractions(movieRepository);
        verifyNoInteractions(movieDtoConverter);
    }

    @Test
    @DisplayName("When deleteMovie Called With Existed Id It Should Return Message")
    public void whenDeleteMovieCalledWithExistedId_itShouldReturnMessage() {
        Movie movie = movieList.get(0);
        int id = movie.getId();

        String expected = ResponseMessages.SUCCESS;

        when(movieRepository.existsById(id)).thenReturn(true);

        String actual = movieService.deleteMovie(id);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).existsById(id);
        verify(movieRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("When deleteMovie Called With Not Existed Id It_Should Throw ResourceNotFoundException")
    public void whenDeleteMovieCalledWithNotExistedId_itShouldThrowResourceNotFoundException() {
        int id = 15;

        String expected = String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE);

        when(movieRepository.existsById(id)).thenReturn(false);

        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(id));
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    @DisplayName("When getLatestMovies Called It Should Return The MovieDto List")
    public void whenGetLatestMoviesCalled_itShouldReturnTheMovieDtoList() {
        List<MovieDto> expected = Collections.singletonList(movieDtoList.get(0));

        when(movieRepository.findAll()).thenReturn(movieList);
        when(movieDtoConverter.convert(movieList.get(0))).thenReturn(expected.get(0));

        List<MovieDto> actual = movieService.getLatestMovies();

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findAll();
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When searchMovies Called With Parameters It Should Return The MovieDto List")
    public void whenSearchMoviesCalledWithParameters_itShouldReturnTheMovieDtoList() {
        Movie movie = movieList.get(0);
        List<Movie> moviesList = Collections.singletonList(movie);
        List<MovieDto> expected = Collections.singletonList(movieDtoList.get(0));

        String title = movie.getTitle();

        when(movieRepository.findByTitleStartingWith(title)).thenReturn(moviesList);
        when(movieDtoConverter.convert(movie)).thenReturn(expected.get(0));

        List<MovieDto> actual = movieService.searchMovies(title);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findByTitleStartingWith(title);
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When calculateStatistics Called It Should Return Statistics")
    public void whenCalculateStatisticsCalled_itShouldReturnStatistics() {
        String key = "mostRatedMovie";

        Map<String, String> statisticsMap = new HashMap<>();
        statisticsMap.put(key, movieList.get(0).getTitle());

        Statistics<String, String> expected = new Statistics<>(ResourceNames.MOVIE, statisticsMap);

        when(movieRepository.findAll()).thenReturn(movieList);

        Statistics<String, String> actual = movieService.calculateStatistics();

        assertEquals(expected.result().get(key), actual.result().get(key));

        verify(movieRepository, times(1)).findAll();
    }
}
