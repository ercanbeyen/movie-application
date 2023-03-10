package com.ercanbeyen.movieapplication.entity;

import com.ercanbeyen.movieapplication.entity.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Movie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String language;
    private int releaseYear;
    private Double rating;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private String summary;
    @ManyToMany(mappedBy = "moviesPlayed", cascade = CascadeType.ALL)
    private Set<Actor> actors = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "director_id", referencedColumnName = "id")
    private Director director;
    //private Integer director_id;
}
