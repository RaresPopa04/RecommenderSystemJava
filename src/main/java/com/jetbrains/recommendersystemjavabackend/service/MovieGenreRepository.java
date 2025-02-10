package com.jetbrains.recommendersystemjavabackend.service;

import com.jetbrains.recommendersystemjavabackend.entity.MovieGenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreRepository extends JpaRepository<MovieGenreEntity, Long> {
}
