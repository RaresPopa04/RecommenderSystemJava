package com.jetbrains.recommendersystemjavabackend.service;


import com.jetbrains.recommendersystemjavabackend.entity.MovieEntity;
import com.jetbrains.recommendersystemjavabackend.entity.MovieGenreEntity;
import com.jetbrains.recommendersystemjavabackend.model.Error;
import com.jetbrains.recommendersystemjavabackend.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieGenreRepository movieGenreRepository;

    public MovieService(MovieRepository movieRepository, MovieGenreRepository movieGenreRepository) {
        this.movieRepository = movieRepository;
        this.movieGenreRepository = movieGenreRepository;
    }

    @Transactional
    protected void saveRecord(CSVRecord record, HashMap<Long, String> movieToImdb, List<Error> errors) {
        try {
            MovieEntity movie = new MovieEntity();
            if (record.get("movieId").isEmpty()) {
                throw new IllegalArgumentException("MovieId cannot be empty for import");
            }
            movie.setFileId(Long.valueOf(record.get("movieId")));
            movie.setTitle(record.get("title"));
            movie.setImdbId(movieToImdb.get(movie.getFileId()));
            movieRepository.save(movie);

            List<MovieGenreEntity> genres = Arrays.stream(record.get("genres").split("\\|"))
                    .map(genre -> {
                        MovieGenreEntity movieGenre = new MovieGenreEntity();
                        movieGenre.setMovie(movie);
                        movieGenre.setGenre(genre);
                        return movieGenre;
                    }).toList();
            movie.setGenres(genres);
            movieGenreRepository.saveAll(genres);
        } catch (Exception e) {
            errors.add(new Error().message(e.getMessage()).id(Integer.valueOf(record.get("movieId"))));
        }
    }
}
