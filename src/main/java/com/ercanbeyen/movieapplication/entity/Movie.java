package com.ercanbeyen.movieapplication.entity;

import com.ercanbeyen.movieapplication.entity.enums.Genre;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class Movie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String title;
    private String language;
    private Integer releaseYear;
    private Double rating;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private String summary;
    @ManyToMany(mappedBy = "moviesPlayed", cascade = CascadeType.ALL)
    private Set<Actor> actors = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "director_id", referencedColumnName = "id")
    private Director director;

    public Movie(String title, String language, Integer releaseYear, Double rating, Genre genre, String summary) {
        this.title = title;
        this.language = language;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.genre = genre;
        this.summary = summary;
    }
}
