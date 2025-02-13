package com.jetbrains.recommendersystemjavabackend.entity;

import com.jetbrains.recommendersystemjavabackend.model.Rating;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Data
@ToString
@Table(name = "ratings")
public class RatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private MovieEntity movie;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Rating toRating() {
        Rating rating = new Rating();
        rating.setRating(this.rating);
        rating.setUserId(this.user.getId());
        rating.setMovieId(this.movie.getId());
        return rating;
    }
}
