package com.jetbrains.recommendersystemjavabackend.service;


import com.jetbrains.recommendersystemjavabackend.entity.MovieEntity;
import com.jetbrains.recommendersystemjavabackend.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Page<MovieEntity> getAllMovies(int page, int size) {
        return movieRepository.findAll(PageRequest.of(page, size));
    }
}
