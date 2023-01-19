package com.ercanbeyen.movieapplication;

import com.ercanbeyen.movieapplication.entity.Movie;
import com.ercanbeyen.movieapplication.entity.enums.Genre;
import com.ercanbeyen.movieapplication.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MovieApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieApplication.class, args);
	}

	/*@Bean
	CommandLineRunner run(MovieRepository movieRepository) {
		return args -> {
			//String title, String language, Integer releaseYear, Double rating, Genre genre, String summary
			movieRepository.save(new Movie("Title 1", "English", 2017, 3.5, Genre.SCIENCE_FICTION, "Summary"));
		};
	}*/
}
