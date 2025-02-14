package com.recommender.repository;

import com.recommender.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

    Optional<RatingEntity> findByUserIdAndMovieId(Long user_id, Long movie_id);
    boolean existsByUserIdAndMovieId(Long user_id, Long movie_id);
}
