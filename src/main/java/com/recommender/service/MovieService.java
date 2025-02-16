package com.recommender.service;


import com.recommender.entity.GenreEntity;
import com.recommender.entity.MovieEntity;
import com.recommender.kafka.AvroProducerService;
import com.recommender.model.Movie;
import com.recommender.model.MoviePage;
import com.recommender.repository.GenreRepository;
import com.recommender.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final AvroProducerService avroProducerService;

    public MoviePage getAllMovies(int page, int size) {

        Page<MovieEntity> movieEntityPage = movieRepository.findAll(PageRequest.of(page, size));
        MoviePage moviePage = new MoviePage();
        moviePage.setContent(movieEntityPage.map(MovieEntity::toMovie).toList());
        moviePage.setTotalPages(movieEntityPage.getTotalPages());
        moviePage.setSize(size);
        moviePage.setNumber(page);
        moviePage.setTotalElements((int) movieEntityPage.getTotalElements());
        return moviePage;
    }

    public Movie getMovieById(Integer id) {
        return movieRepository.findById(Long.valueOf(id)).map(MovieEntity::toMovie).orElse(null);
    }


    public void deleteMovieById(Integer id) {
        movieRepository.deleteById(Long.valueOf(id));
    }


    public Movie updateMovie(Integer id, Movie movie) {
        Optional<MovieEntity> movieEntity = movieRepository.findById(Long.valueOf(id));
        if (movieEntity.isEmpty()) {
            return null;
        }

        if (movieRepository.existsByImdbId(movie.getImdbId()) && !movieEntity.get().getImdbId().equals(movie.getImdbId())) {
            throw new IllegalArgumentException("Movie imdb already exists");
        }

        if (movieRepository.existsByTitle(movie.getTitle()) && !movieEntity.get().getTitle().equals(movie.getTitle())) {
            throw new IllegalArgumentException("Movie title already exists");
        }

        MovieEntity currentMovieEntity = movieEntity.get();
        currentMovieEntity.setTitle(movie.getTitle());
        currentMovieEntity.setImdbId(movie.getImdbId());

        movie.getGenres().forEach(genre -> {
            Optional<GenreEntity> foundGenre = genreRepository.findById(genre);
            if (foundGenre.isEmpty()) {
                throw new IllegalArgumentException("Genre not found");
            }
        });

        avroProducerService.sendMovieEvent(currentMovieEntity.getFileId(), movie.getGenres());
        currentMovieEntity.setGenres(genreRepository.findAllById(movie.getGenres()));
        return movieRepository.save(currentMovieEntity).toMovie();

    }


    public Movie createMovie(Movie movie) {
        if (movieRepository.existsByImdbId(movie.getImdbId())) {
            throw new IllegalArgumentException("Movie imdb already exists");
        }
        if (movieRepository.existsByTitle(movie.getTitle())) {
            throw new IllegalArgumentException("Movie title already exists");
        }

        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setTitle(movie.getTitle());
        movieEntity.setImdbId(movie.getImdbId());
        movieEntity.setFileId(movieRepository.findMaxId() + 1);

        movie.getGenres().forEach(genre -> {
            Optional<GenreEntity> foundGenre = genreRepository.findById(genre);
            if (foundGenre.isEmpty()) {
                throw new IllegalArgumentException("Genre not found");
            }
        });

        movieEntity.setGenres(genreRepository.findAllById(movie.getGenres()));
        Movie saved = movieRepository.save(movieEntity).toMovie();
        avroProducerService.sendMovieEvent(movieEntity.getFileId(),  movie.getGenres());
        return saved;
    }
}
