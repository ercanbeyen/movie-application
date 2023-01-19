package com.ercanbeyen.movieapplication.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Data
@Entity
public class Actor extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "actors_movies",
            joinColumns = {
                    @JoinColumn(name = "actor_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "movie_id")
            }
    )
    private Set<Movie> moviesPlayed = new HashSet<>();
}
