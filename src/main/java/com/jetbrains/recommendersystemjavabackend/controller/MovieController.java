package com.jetbrains.recommendersystemjavabackend.controller;

import com.jetbrains.recommendersystemjavabackend.api.MoviesApi;
import com.jetbrains.recommendersystemjavabackend.entity.MovieEntity;
import com.jetbrains.recommendersystemjavabackend.model.Movie;
import com.jetbrains.recommendersystemjavabackend.model.MoviePage;
import com.jetbrains.recommendersystemjavabackend.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class MovieController implements MoviesApi {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public ResponseEntity<MoviePage> moviesGet(Integer page, Integer size) {

        Page<MovieEntity> movieEntityPage = movieService.getAllMovies(page, size);
        MoviePage moviePage = new MoviePage();
        moviePage.setContent(movieEntityPage.map(MovieEntity::toMovie).toList());
        moviePage.setTotalPages(movieEntityPage.getTotalPages());
        moviePage.setTotalElements((int) movieEntityPage.getTotalElements());

        return ResponseEntity.ok(moviePage);
    }

    @Override
    public ResponseEntity<Movie> moviesPost(Movie movie) {
        return ResponseEntity.ok(movieService.createMovie(movie));
    }

    @Override
    public ResponseEntity<Movie> moviesIdGet(Integer id) {
        Optional<Movie> movie = Optional.ofNullable(movieService.getMovieById(id));
        if (movie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @Override
    public ResponseEntity<Void> moviesIdDelete(Integer id) {
        Optional<Movie> movie = Optional.ofNullable(movieService.getMovieById(id));
        if (movie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        movieService.deleteMovieById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Movie> moviesIdPut(Integer id, Movie movie) {
        Optional<Movie> movieFound = Optional.ofNullable(movieService.getMovieById(id));
        if (movieFound.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }
}
