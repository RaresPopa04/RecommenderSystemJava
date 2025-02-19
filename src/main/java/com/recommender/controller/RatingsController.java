package com.recommender.controller;

import com.recommender.api.RatingsApi;
import com.recommender.entity.RatingEntity;
import com.recommender.model.Rating;
import com.recommender.model.RatingPut;
import com.recommender.repository.RatingRepository;
import com.recommender.service.RatingService;
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
    public ResponseEntity<Rating> ratingsUserIdMovieIdPut(Long userId, Long movieId, RatingPut rating) {
        Optional<RatingEntity> ratingEntity = ratingsRepository.findByUserIdAndMovieId(userId, movieId);
        if (ratingEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ratingService.updateRating(userId, movieId, rating));
    }

    @Override
    public ResponseEntity<Rating> ratingsPost(Rating rating) {
        return ResponseEntity.ok(ratingService.createRating(rating));
    }
}
