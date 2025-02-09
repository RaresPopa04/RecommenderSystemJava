package com.jetbrains.recommendersystemjavabackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "imdb_id", nullable = false, length = 50)
    private String imdbId;

    @OneToMany(mappedBy = "movie")
    private List<MovieGenreEntity> genres;

    @OneToMany(mappedBy = "movie")
    private List<RatingEntity> ratings;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
