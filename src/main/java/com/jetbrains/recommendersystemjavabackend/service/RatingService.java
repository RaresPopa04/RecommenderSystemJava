package com.jetbrains.recommendersystemjavabackend.service;


import com.jetbrains.recommendersystemjavabackend.entity.RatingEntity;
import com.jetbrains.recommendersystemjavabackend.model.Rating;
import com.jetbrains.recommendersystemjavabackend.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public void deleteRating(Long userId, Long movieId) {
        ratingRepository.findByUserIdAndMovieId(userId, movieId).ifPresent(ratingRepository::delete);
    }

    public RatingEntity updateRating(Long userId, Long movieId, Rating rating) {
        return ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .map(ratingEntity -> {
                    ratingEntity.setRating(rating.getRating());
                    return ratingRepository.save(ratingEntity);
                })
                .orElse(null);
    }
}
