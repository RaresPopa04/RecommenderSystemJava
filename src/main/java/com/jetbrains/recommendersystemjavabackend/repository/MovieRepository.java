package com.jetbrains.recommendersystemjavabackend.repository;

import com.jetbrains.recommendersystemjavabackend.entity.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    Optional<MovieEntity> findByFileId(Long fileId);
    Page<MovieEntity> findAll(Pageable pageable);
    boolean existsByTitle(String title);
    boolean existsByImdbId(String imdbId);

    @Query("SELECT  max(movie.fileId) from MovieEntity movie")
    Long findMaxId();
}
