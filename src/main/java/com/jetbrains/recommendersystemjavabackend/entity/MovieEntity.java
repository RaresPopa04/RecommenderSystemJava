package com.jetbrains.recommendersystemjavabackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jetbrains.recommendersystemjavabackend.model.Movie;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movies")
@Data
@ToString
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
    @JsonManagedReference
    private List<RatingEntity> ratings;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Movie toMovie() {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.genres(genres.stream().map(GenreEntity::getName).toList());
        movie.setImdbId(imdbId);
        return movie;
    }
}
