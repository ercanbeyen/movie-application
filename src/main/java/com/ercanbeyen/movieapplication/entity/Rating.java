package com.ercanbeyen.movieapplication.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ratings")
@Data
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double value;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Movie movie;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Audience audience;
}
