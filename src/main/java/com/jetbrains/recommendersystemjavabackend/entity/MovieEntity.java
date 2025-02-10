package com.jetbrains.recommendersystemjavabackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id", unique = true)
    private Long fileId;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "imdb_id", nullable = false, length = 50)
    private String imdbId;

    @ManyToMany
    @JoinTable(
            name = "movie_to_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_name")
    )
    List<GenreEntity> genres;

    @OneToMany(mappedBy = "movie")
    private List<RatingEntity> ratings;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
