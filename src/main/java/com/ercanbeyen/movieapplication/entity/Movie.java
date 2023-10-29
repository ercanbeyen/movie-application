package com.ercanbeyen.movieapplication.entity;

import com.ercanbeyen.movieapplication.constant.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String imdbId;
    private String title;
    private String language;
    private Integer releaseYear;
    private Double averageRating;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private String summary;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "actors_movies",
            joinColumns = {@JoinColumn(name = "actor_id")},
            inverseJoinColumns = {@JoinColumn(name = "movie_id")}
    )
    private Set<Actor> actors = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "director_id", referencedColumnName = "id")
    private Director director;
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @Override
    public String toString() {
        Integer directorId = (director != null) ? director.getId() : null;

        List<Integer> actorIdList = actors.stream()
                .map(Actor::getId)
                .toList();

        List<Integer> ratingList = ratings.stream()
                .map(Rating::getId)
                .toList();

        return "Movie{" +
                "id=" + id +
                ", imdbId='" + imdbId + '\'' +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", releaseYear=" + releaseYear +
                ", averageRating=" + averageRating +
                ", genre=" + genre +
                ", summary='" + summary + '\'' +
                ", actors=" + actorIdList +
                ", director=" + directorId +
                ", ratings=" +  ratingList +
                '}';
    }
}