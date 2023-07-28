package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.OrderBy;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.MovieFilteringOptions;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.constant.enums.Genre;
import com.ercanbeyen.movieapplication.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private List<Movie> movieList;
    private List<MovieDto> movieDtoList;

    private Director getMockDirector() {
        return Director.builder()
                .id(1)
                .name("Test-name")
                .surname("Test-surname")
                .birthYear(LocalDate.of(2005, 2, 12))
                .nationality("Test-nationality")
                .biography("Test-biography")
                .moviesDirected(new ArrayList<>())
                .build();
    }

    private List<Movie> getMockMovieList() {
        int id = 1;
        String title = "Test-title";
        Genre genre = Genre.SCIENCE_FICTION;
        String summary = "Test-summary";
        Director director = getMockDirector();
        Set<Actor> actors = new HashSet<>();

        Movie movie1 = Movie.builder()
                .id(id)
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
    @DisplayName("When CreateMovie Called With Valid Input It Should Return MovieDto")
    public void whenCreateMovieCalledWithValidInput_itShouldReturnMovieDto() {
        Movie movie = movieList.get(0);
        MovieDto expected = movieDtoList.get(0);
        int directorId = expected.getDirectorId();

        CreateMovieRequest request = new CreateMovieRequest();
        request.setTitle(movie.getTitle());
        request.setGenre(movie.getGenre());
        request.setReleaseYear(movie.getReleaseYear());
        request.setLanguage(movie.getLanguage());
        request.setDirectorId(expected.getDirectorId());
        request.setRating(expected.getRating());
        request.setSummary(movie.getSummary());


        when(directorService.getDirectorById(directorId)).thenReturn(movie.getDirector());
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        when(movieDtoConverter.convert(movie)).thenReturn(expected);

        MovieDto actual = movieService.createMovie(request);

        assertEquals(expected, actual);

        verify(directorService, times(1)).getDirectorById(directorId);
        verify(movieRepository, times(1)).save(any(Movie.class));
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When GetMovie Called With Valid Input It Should Return MovieDto")
    public void whenGetMovieCalledValidInput_itShouldReturnMovieDto() {
        Movie movie = movieList.get(0);
        int id = movie.getId();

        MovieDto expected = movieDtoList.get(0);
        expected.setId(id);
        Optional<Movie> optionalMovie = Optional.of(movie);

        when(movieRepository.findById(id)).thenReturn(optionalMovie);
        when(movieDtoConverter.convert(movie)).thenReturn(expected);

        MovieDto actual = movieService.getMovie(id);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findById(id);
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When Get Movies Called It Should Return MovieDto List")
    public void whenGetMoviesCalledItShouldReturnMovieDto() {
        String language = "English";

        List<MovieDto> expected = Collections.singletonList(movieDtoList.get(0));

        when(movieRepository.findAll()).thenReturn(movieList);
        when(movieDtoConverter.convert(movieList.get(0))).thenReturn(expected.get(0));

        MovieFilteringOptions movieFilteringOptions = new MovieFilteringOptions();
        movieFilteringOptions.setLanguage(language);

        List<MovieDto> actual = movieService.getMovies(movieFilteringOptions, OrderBy.ASC);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findAll();
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When UpdateMovie Called With Valid Inputs It Should Return MovieDto")
    public void whenUpdateMovieCalledWithValidInputs_itShouldReturnMovieDto() {
        Movie movie = movieList.get(0);
        MovieDto expected = movieDtoList.get(1);
        int id = movie.getId();
        expected.setId(id);

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setTitle(expected.getTitle());
        request.setGenre(expected.getGenre());
        request.setReleaseYear(expected.getReleaseYear());
        request.setLanguage(expected.getLanguage());
        request.setRating(expected.getRating());
        request.setSummary(expected.getSummary());



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
    @DisplayName("When GetLatestMovies Called With Valid Input It Should Return The MovieDto List")
    public void whenGetLatestMoviesCalledWithValidInput_itShouldReturnTheMovieDtoList() {
        List<MovieDto> expected = Collections.singletonList(movieDtoList.get(0));

        when(movieRepository.findAll()).thenReturn(movieList);
        when(movieDtoConverter.convert(movieList.get(0))).thenReturn(expected.get(0));

        List<MovieDto> actual = movieService.getLatestMovies();

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findAll();
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When SearchMovies Called With Valid Input It Should Return The MovieDto List")
    public void whenSearchMoviesCalledWithValidInput_itShouldReturnTheMovieDtoList() {
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

}
