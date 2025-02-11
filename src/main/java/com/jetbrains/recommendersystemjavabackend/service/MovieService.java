package com.jetbrains.recommendersystemjavabackend.service;


import com.jetbrains.recommendersystemjavabackend.entity.GenreEntity;
import com.jetbrains.recommendersystemjavabackend.entity.MovieEntity;
import com.jetbrains.recommendersystemjavabackend.model.Movie;
import com.jetbrains.recommendersystemjavabackend.repository.GenreRepository;
import com.jetbrains.recommendersystemjavabackend.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public MovieService(MovieRepository movieRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    public Page<MovieEntity> getAllMovies(int page, int size) {
        return movieRepository.findAll(PageRequest.of(page, size));
    }

    public Movie getMovieById(Integer id) {
        return movieRepository.findById(Long.valueOf(id)).map(MovieEntity::toMovie).orElse(null);
    }

    @Transactional
    public void deleteMovieById(Integer id) {
        movieRepository.deleteById(Long.valueOf(id));
    }

    @Transactional
    public Movie updateMovie(Integer id, Movie movie) {
        Optional<MovieEntity> movieEntity = movieRepository.findById(Long.valueOf(id));
        if (movieEntity.isEmpty()) {
            return null;
        }
        MovieEntity updatedMovieEntity = movieEntity.get();
        updatedMovieEntity.setTitle(movie.getTitle());
        updatedMovieEntity.setImdbId(movie.getImdbId());
        updatedMovieEntity.setImdbId(movie.getImdbId());

        for(String genre : movie.getGenres()){
            Optional<GenreEntity> foundGenre = genreRepository.findById(genre);
            if(foundGenre.isEmpty()){
                throw new IllegalArgumentException("Genre not found");
            }
        }
        updatedMovieEntity.setGenres(genreRepository.findAllById(movie.getGenres()));
        updatedMovieEntity.setRatings(movieEntity.get().getRatings());
        movieRepository.save(updatedMovieEntity);
        return updatedMovieEntity.toMovie();

    }
}
