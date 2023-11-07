package com.ercanbeyen.movieapplication.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double rate;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Movie movie;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Audience audience;

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", rate=" + rate +
                ", movie=" + movie.getId() +
                ", audience=" + audience.getId() +
                '}';
    }
}
