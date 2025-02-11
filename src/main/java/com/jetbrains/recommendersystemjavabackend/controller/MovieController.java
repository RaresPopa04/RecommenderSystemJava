package com.jetbrains.recommendersystemjavabackend.controller;

import com.jetbrains.recommendersystemjavabackend.api.MoviesApi;
import com.jetbrains.recommendersystemjavabackend.entity.MovieEntity;
import com.jetbrains.recommendersystemjavabackend.model.MoviePage;
import com.jetbrains.recommendersystemjavabackend.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController implements MoviesApi {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public ResponseEntity<MoviePage> moviesGet(Integer page, Integer size
    ) {

        Page<MovieEntity> movieEntityPage = movieService.getAllMovies(page, size);
        MoviePage moviePage = new MoviePage();
        moviePage.setContent(movieEntityPage.map(MovieEntity::toMovie).toList());
        moviePage.setTotalPages(movieEntityPage.getTotalPages());
        moviePage.setTotalElements((int) movieEntityPage.getTotalElements());

        return ResponseEntity.ok(moviePage);
    }
}
