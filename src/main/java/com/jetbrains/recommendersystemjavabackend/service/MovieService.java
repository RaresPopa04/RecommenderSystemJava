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

        if(movieRepository.existsByImdbId(movie.getImdbId()) && !movieEntity.get().getImdbId().equals(movie.getImdbId())){
            throw new IllegalArgumentException("Movie imdb already exists");
        }

        if(movieRepository.existsByTitle(movie.getTitle()) && !movieEntity.get().getTitle().equals(movie.getTitle())){
            throw new IllegalArgumentException("Movie title already exists");
        }

        MovieEntity currentMovieEntity = movieEntity.get();
        currentMovieEntity.setTitle(movie.getTitle());
        currentMovieEntity.setImdbId(movie.getImdbId());

        movie.getGenres().forEach(genre -> {
            Optional<GenreEntity> foundGenre = genreRepository.findById(genre);
            if(foundGenre.isEmpty()){
                throw new IllegalArgumentException("Genre not found");
            }
        });

        currentMovieEntity.setGenres(genreRepository.findAllById(movie.getGenres()));
        return movieRepository.save(currentMovieEntity).toMovie();

    }

    public Movie createMovie(Movie movie) {
        if(movieRepository.existsByImdbId(movie.getImdbId())){
            throw new IllegalArgumentException("Movie imdb already exists");
        }
        if(movieRepository.existsByTitle(movie.getTitle())){
            throw new IllegalArgumentException("Movie title already exists");
        }

        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle(movie.getTitle());
        movieEntity.setImdbId(movie.getImdbId());

        movie.getGenres().forEach(genre -> {
            Optional<GenreEntity> foundGenre = genreRepository.findById(genre);
            if(foundGenre.isEmpty()){
                throw new IllegalArgumentException("Genre not found");
            }
        });

        movieEntity.setGenres(genreRepository.findAllById(movie.getGenres()));
        return movieRepository.save(movieEntity).toMovie();
    }
}
