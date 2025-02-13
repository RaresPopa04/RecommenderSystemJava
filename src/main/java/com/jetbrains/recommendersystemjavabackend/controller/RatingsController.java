package com.jetbrains.recommendersystemjavabackend.controller;

import com.jetbrains.recommendersystemjavabackend.api.RatingsApi;
import com.jetbrains.recommendersystemjavabackend.entity.RatingEntity;
import com.jetbrains.recommendersystemjavabackend.model.Rating;
import com.jetbrains.recommendersystemjavabackend.repository.RatingRepository;
import com.jetbrains.recommendersystemjavabackend.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class RatingsController implements RatingsApi {

    private final RatingService ratingService;
    private final RatingRepository ratingsRepository;

    public RatingsController(RatingService ratingService, RatingRepository ratingsRepository) {
        this.ratingService = ratingService;
        this.ratingsRepository = ratingsRepository;
    }

    @Override
    public ResponseEntity<Void> ratingsUserIdMovieIdDelete(Long userId, Long movieId) {
        Optional<RatingEntity> ratingEntity = ratingsRepository.findByUserIdAndMovieId(userId, movieId);
        if (ratingEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ratingService.deleteRating(userId, movieId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Rating> ratingsUserIdMovieIdGet(Long userId, Long movieId) {
        Optional<RatingEntity> ratingEntity = ratingsRepository.findByUserIdAndMovieId(userId, movieId);
        return ratingEntity.map(entity -> ResponseEntity.ok(entity.toRating())).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @Override
    public ResponseEntity<Rating> ratingsUserIdMovieIdPut(Long userId, Long movieId, Rating rating) {
        Optional<RatingEntity> ratingEntity = ratingsRepository.findByUserIdAndMovieId(userId, movieId);
        if (ratingEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ratingService.updateRating(userId, movieId, rating).toRating());
    }
}
