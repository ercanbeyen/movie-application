package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.message.ActionMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.dto.option.filter.MovieFilteringOptions;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

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
    @DisplayName("When CreateMovie Called With Valid Request It Should Return MovieDto")
    public void whenCreateMovieCalledWithValidRequest_itShouldReturnMovieDto() {
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
    @DisplayName("When GetMovie Called With Existed Id It Should Return MovieDto")
    public void whenGetMovieCalledExistedId_itShouldReturnMovieDto() {
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
    @DisplayName("When GetMovie Called With Not Existed Id It Should Throw ResourceNotFoundException")
    public void whenGetMovieCalledWithNotExistedId_itShouldThrowResourceNotFoundException() {
        int id = 15;
        Optional<Movie> movieOptional = Optional.empty();

        when(movieRepository.findById(id)).thenReturn(movieOptional);

        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.getMovie(id));
        String expected = exception.getMessage();

        String actual = String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE, id);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findById(id);
        verifyNoMoreInteractions(movieRepository);
        verifyNoInteractions(movieDtoConverter);
    }

    @Test
    @DisplayName("When Get Movies Called With Parameters It Should Return MovieDto List")
    public void whenGetMoviesCalledWithParameters_itShouldReturnMovieDto() {
        Pageable pageable = Pageable.ofSize(1).withPage(0);

        List<Movie> fetchedMovieList = Collections.singletonList(movieList.get(0));
        List<MovieDto> fetchedMovieDtoList = Collections.singletonList(movieDtoList.get(0));

        Page<Movie> moviePage = new PageImpl<>(fetchedMovieList, pageable, fetchedMovieList.size());
        PageDto<Movie, MovieDto> expected = new PageDto<>(moviePage, fetchedMovieDtoList);

        when(movieRepository.findAll(pageable)).thenReturn(moviePage);
        when(movieRepository.count()).thenReturn(Long.valueOf(movieList.size()));
        when(movieDtoConverter.convert(movieList.get(0))).thenReturn(movieDtoList.get(0));

        MovieFilteringOptions movieFilteringOptions = new MovieFilteringOptions();
        movieFilteringOptions.setLanguage(movieList.get(0).getLanguage());

        PageDto<Movie, MovieDto> actual = movieService.filterMovies(movieFilteringOptions, null, null, pageable);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findAll(pageable);
        verify(movieRepository, times(1)).count();
        verify(movieDtoConverter, times(1)).convert(any(Movie.class));
    }

    @Test
    @DisplayName("When UpdateMovie Called With Existed Id And Valid Request It Should Return MovieDto")
    public void whenUpdateMovieCalledWithExistedIdAndValidRequest_itShouldReturnMovieDto() {
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
    @DisplayName("When UpdateMovie Called With Not Existed Id It Should Throw ResourceNotFoundException")
    public void whenUpdateMovieCalledWithNotExistedId_itShouldThrowResourceNotFoundException() {
        int id = 15;
        Optional<Movie> movieOptional = Optional.empty();

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setTitle("Test-title");
        request.setLanguage("Test-language");
        request.setRating(2d);
        request.setSummary("Test-summary");

        String expected = String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE, id);

        when(movieRepository.findById(id)).thenReturn(movieOptional);

        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.updateMovie(id, request));
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).findById(id);
        verifyNoMoreInteractions(movieRepository);
        verifyNoInteractions(movieDtoConverter);
    }

    @Test
    @DisplayName("When DeleteMovie Called With Existed Id It Should Return Message")
    public void whenDeleteMovieCalledWithExistedId_itShouldReturnMessage() {
        Movie movie = movieList.get(0);
        int id = movie.getId();

        String expected = String.format(ResponseMessages.SUCCESS, ResourceNames.MOVIE, id, ActionMessages.DELETED);

        when(movieRepository.existsById(id)).thenReturn(true);

        String actual = movieService.deleteMovie(id);

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).existsById(id);
        verify(movieRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("When DeleteMovie Called With Not Existed Id It_Should Throw ResourceNotFoundException")
    public void whenDeleteMovieCalledWithNotExistedId_itShouldThrowResourceNotFoundException() {
        int id = 15;

        String expected = String.format(ResponseMessages.NOT_FOUND, ResourceNames.MOVIE, id);

        when(movieRepository.existsById(id)).thenReturn(false);

        RuntimeException exception = assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(id));
        String actual = exception.getMessage();

        assertEquals(expected, actual);

        verify(movieRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    @DisplayName("When GetLatestMovies Called It Should Return The MovieDto List")
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
    @DisplayName("When SearchMovies Called With Parameters It Should Return The MovieDto List")
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

}
