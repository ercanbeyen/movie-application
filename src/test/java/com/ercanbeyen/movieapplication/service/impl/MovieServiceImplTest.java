package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.dto.MovieDto;
import com.ercanbeyen.movieapplication.dto.converter.MovieDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateMovieRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateMovieRequest;
import com.ercanbeyen.movieapplication.entity.Actor;
import com.ercanbeyen.movieapplication.entity.Director;
import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.enums.Genre;
import com.ercanbeyen.movieapplication.repository.MovieRepository;
import com.ercanbeyen.movieapplication.service.DirectorService;
import com.ercanbeyen.movieapplication.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
//@RequiredArgsConstructor
public class MovieServiceImplTest {
    @InjectMocks
    private MovieServiceImpl movieService;
    @Mock
    private MovieRepository movieRepository;
    @Spy
    private MovieDtoConverter movieDtoConverter;
    @Mock
    private DirectorServiceImpl directorService;

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

    private List<MovieDto> getMockMovieDtos() {
        Integer id = 1;
        Integer directorId = 1;
        String title = "Test-title";
        Genre genre = Genre.SCIENCE_FICTION;
        String summary = "Test-summary";
        Set<Integer> actorIds = new HashSet<>();

        MovieDto movieDto1 = MovieDto.builder()
                .id(id)
                .title(title)
                .genre(genre)
                .rating(3.4)
                .releaseYear(2022)
                .language("English")
                .summary(summary)
                .directorId(directorId)
                .actorsIds(actorIds)
                .build();

        id++;

        MovieDto movieDto2 = MovieDto.builder()
                .id(id)
                .title(title)
                .genre(genre)
                .rating(2.7)
                .releaseYear(2015)
                .language("Spanish")
                .summary(summary)
                .directorId(directorId)
                .actorsIds(actorIds)
                .build();

        return Arrays.asList(movieDto1, movieDto2);
    }

    private List<Movie> getMockMovies() {
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

    @Test
    @DisplayName("When CreateMovie Called With Valid Input It Should Return MovieDto")
    public void whenCreateMovieCalledWithValidInput_itShouldReturnMovieDto() {
        Movie movie = getMockMovies().get(0);
        MovieDto movieDto = getMockMovieDtos().get(0);
        int id = movie.getId();
        int directorId = movieDto.getDirectorId();

        CreateMovieRequest request = new CreateMovieRequest();
        request.setTitle(movie.getTitle());
        request.setGenre(movie.getGenre());
        request.setReleaseYear(movie.getReleaseYear());
        request.setLanguage(movie.getLanguage());
        request.setDirectorId(movieDto.getDirectorId());
        request.setRating(movieDto.getRating());
        request.setSummary(movie.getSummary());


        Mockito.when(directorService.getDirectorById(directorId)).thenReturn(movie.getDirector());
        //Mockito.lenient().when(movieRepository.save(movie)).thenReturn(movie);
        //Mockito.when(movieRepository.save(movie)).thenReturn(movie);
        Mockito.when(movieRepository.save(any(Movie.class))).thenReturn(movie);
        //Mockito.doReturn(movie).when(movieRepository.save(movie));
        //Mockito.when(movieDtoConverter.convert(movie)).thenReturn(movieDto);
        //Mockito.lenient().when(movieDtoConverter.convert(movie)).thenReturn(movieDto);

        MovieDto result = movieService.createMovie(request);

        assertEquals(movieDto, result);

        Mockito.verify(directorService).getDirectorById(directorId);
        Mockito.verify(movieRepository).save(any(Movie.class));
        //Mockito.verify(movieDtoConverter).convert(movie);
    }

    @Test
    @DisplayName("When GetMovie Called With Valid Input It Should Return MovieDto")
    public void whenGetMovieCalledValidInput_itShouldReturnMovieDto() {
        Movie movie = getMockMovies().get(0);
        int id = movie.getId();

        MovieDto movieDto = getMockMovieDtos().get(0);
        movieDto.setId(id);
        Optional<Movie> optionalMovie = Optional.of(movie);

        Mockito.when(movieRepository.findById(id)).thenReturn(optionalMovie);
        //Mockito.when(movieDtoConverter.convert(movie)).thenReturn(movieDto);

        MovieDto result = movieService.getMovie(id);

        assertEquals(movieDto, result);

        Mockito.verify(movieRepository).findById(id);
        //Mockito.verify(movieDtoConverter).convert(movie);
    }

    @Test
    @DisplayName("When Get Movies Called It Should Return MovieDtos")
    public void whenGetMoviesCalledItShouldReturnMovieDto() {
        String language = "English";

        List<Movie> movies = getMockMovies();
        List<MovieDto> movieDtos = Collections.singletonList(getMockMovieDtos().get(0));

        Mockito.when(movieRepository.findAll()).thenReturn(movies);

        List<MovieDto> result = movieService.getMovies(language, null, null, null, null, null);

        assertEquals(movieDtos, result);

        Mockito.verify(movieRepository).findAll();
    }

    @Test
    @DisplayName("When UpdateMovie Called With Valid Inputs It Should Return MovieDto")
    public void whenUpdateMovieCalledWithValidInputs_itShouldReturnMovieDto() {
        Movie movie = getMockMovies().get(0);
        MovieDto movieDto = getMockMovieDtos().get(1);
        int id = movie.getId();
        movieDto.setId(id);

        UpdateMovieRequest request = new UpdateMovieRequest();
        request.setTitle(movieDto.getTitle());
        request.setGenre(movieDto.getGenre());
        request.setReleaseYear(movieDto.getReleaseYear());
        request.setLanguage(movieDto.getLanguage());
        request.setRating(movieDto.getRating());
        request.setSummary(movieDto.getSummary());



        Mockito.when(movieRepository.findById(id)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.save(Mockito.any(Movie.class))).thenReturn(movie);

        MovieDto result = movieService.updateMovie(id, request);

        assertEquals(movieDto, result);

        Mockito.verify(movieRepository).findById(id);
        Mockito.verify(movieRepository).save(Mockito.any(Movie.class));
    }

    @Test
    @DisplayName("When GetLatestMovies Called With Valid Input It Should Return The MovieDtos")
    public void whenGetLatestMoviesCalledWithValidInput_itShouldReturnTheMovieDtos() {
        List<Movie> movies = getMockMovies();
        List<MovieDto> movieDtos = Collections.singletonList(getMockMovieDtos().get(0));

        Mockito.when(movieRepository.findAll()).thenReturn(movies);

        List<MovieDto> result = movieService.getLatestMovies();

        assertEquals(movieDtos, result);

        Mockito.verify(movieRepository).findAll();
    }

    @Test
    @DisplayName("When SearchMovies Called With Valid Input It Should Return The MovieDtos")
    public void whenSearchMoviesCalledWithValidInput_itShouldReturnTheMovieDtos() {
        Movie movie = getMockMovies().get(0);
        List<Movie> movies = Collections.singletonList(movie);
        List<MovieDto> movieDtos = Collections.singletonList(getMockMovieDtos().get(0));

        String title = movie.getTitle();

        Mockito.when(movieRepository.findByTitleStartingWith(title)).thenReturn(movies);

        List<MovieDto> result = movieService.searchMovies(title);

        assertEquals(movieDtos, result);

        Mockito.verify(movieRepository).findByTitleStartingWith(title);
    }

}
