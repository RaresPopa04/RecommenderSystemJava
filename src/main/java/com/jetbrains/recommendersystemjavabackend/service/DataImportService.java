package com.jetbrains.recommendersystemjavabackend.service;


import com.jetbrains.recommendersystemjavabackend.entity.GenreEntity;
import com.jetbrains.recommendersystemjavabackend.entity.MovieEntity;
import com.jetbrains.recommendersystemjavabackend.entity.RatingEntity;
import com.jetbrains.recommendersystemjavabackend.entity.UserEntity;
import com.jetbrains.recommendersystemjavabackend.model.Error;
import com.jetbrains.recommendersystemjavabackend.repository.GenreRepository;
import com.jetbrains.recommendersystemjavabackend.repository.MovieRepository;
import com.jetbrains.recommendersystemjavabackend.repository.RatingRepository;
import com.jetbrains.recommendersystemjavabackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataImportService {

    private static final int BATCH_SIZE = 100;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DataImportService.class);
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    public DataImportService(GenreRepository genreRepository, MovieRepository movieRepository, UserRepository userRepository, RatingRepository ratingRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional
    public void importMovies(Iterable<CSVRecord> movieGenreRecords, Iterable<CSVRecord> imdbIdRecords, List<Error> errors) {
        HashMap<Long, String> movieToImdb = getImdbForMovie(imdbIdRecords);
        HashMap<String, GenreEntity> genreMap = new HashMap<>();
        List<MovieEntity> movies = new ArrayList<>();
        for (CSVRecord record : movieGenreRecords) {
            try{
                parseGenres(record, genreMap);
                MovieEntity movie = parseMovie(record, movieToImdb, genreMap);
                movies.add(movie);

                log.debug("Parsed movie: {}", movie);

                if (movies.size() >= BATCH_SIZE) {
                    movieRepository.saveAll(movies);
                    movieRepository.flush();
                    movies.clear();
                }

            }catch (Exception e){
                errors.add(new Error().id(Integer.valueOf(record.get("movieId"))).message(e.getMessage()));
            }
        }

        if (!movies.isEmpty()) {
            movieRepository.saveAll(movies);
        }
    }

    @Transactional
    public void importUsers(Iterable<CSVRecord> userRecords, List<Error> errors) {
        HashSet<Integer> fileIdsInserted = new HashSet<>();
        List<UserEntity> users = new ArrayList<>();
        for (CSVRecord record : userRecords) {
            try {
                if (fileIdsInserted.contains(Integer.valueOf(record.get("userId")))) {
                    continue;
                }
                UserEntity user = new UserEntity();
                user.setFileId(Long.valueOf(record.get("userId")));
                fileIdsInserted.add(Integer.valueOf(record.get("userId")));
                user.setUsername(record.get("userId") + "_user");
                user.setPassword("password");
                users.add(user);

                log.debug("Parsed user: {}", user);

                if (users.size() >= BATCH_SIZE) {
                    userRepository.saveAll(users);
                    userRepository.flush();
                    users.clear();
                }

            } catch (Exception e) {
                errors.add(new Error().id(Integer.valueOf(record.get("userId"))).message(e.getMessage()));
            }
        }

        if(!users.isEmpty()){
            userRepository.saveAll(users);
        }
    }

    @Transactional
    public void importRatings(Iterable<CSVRecord> ratingRecords, List<Error> errors) {
        List<RatingEntity> ratingEntities = new ArrayList<>();
        HashMap<Long, UserEntity> userMap = getUserMap();
        int count = 0;
        HashMap<Long, MovieEntity> movieMap = getMovieMap();
        for(CSVRecord record : ratingRecords){
            try{
                RatingEntity ratingEntity = new RatingEntity();
                Long userId = Long.valueOf(record.get("userId"));
                Long movieId = Long.valueOf(record.get("movieId"));

                if(!userMap.containsKey(userId)){
                    errors.add(new Error().id(Integer.valueOf(record.get("userId"))).message("User not found"));
                    continue;
                }
                if(!movieMap.containsKey(movieId)){
                    errors.add(new Error().id(Integer.valueOf(record.get("movieId"))).message("Movie not found"));
                    continue;
                }
                ratingEntity.setUser(userMap.get(userId));
                ratingEntity.setMovie(movieMap.get(movieId));
                ratingEntity.setRating(Double.valueOf(record.get("rating")));

                ratingEntities.add(ratingEntity);

                count++;
                log.debug("Parsed rating: {}, with count: {}", ratingEntity, count);
                log.info("Count: {}", count);
                if(ratingEntities.size() >= BATCH_SIZE){
                    ratingRepository.saveAll(ratingEntities);
                    ratingRepository.flush();
                    ratingEntities.clear();
                }
            }catch (Exception e){
                errors.add(new Error().id(Integer.valueOf(record.get("userId"))).message(e.getMessage()));
            }
        }

        if(!ratingEntities.isEmpty()){
            ratingRepository.saveAll(ratingEntities);
        }
    }

    private HashMap<Long, MovieEntity> getMovieMap() {
        HashMap<Long, MovieEntity> movieMap = new HashMap<>();
        movieRepository.findAll().forEach(movie -> movieMap.put(movie.getFileId(), movie));
        return movieMap;
    }

    private HashMap<Long, UserEntity> getUserMap() {
        HashMap<Long, UserEntity> userMap = new HashMap<>();
        userRepository.findAll().forEach(user -> userMap.put(user.getFileId(), user));
        return userMap;
    }


    private void parseGenres(CSVRecord record, HashMap<String, GenreEntity> genreMap) {
        if (record.get("genres").isEmpty()) {
            return;
        }
        if (record.get("genres").equals("(no genres listed)")) {
            return;
        }
        String[] genreNames = record.get("genres").split("\\|");
        for (String genreName : genreNames) {
            if (!genreMap.containsKey(genreName)) {
                GenreEntity genre = new GenreEntity();
                genre.setName(genreName);
                genreMap.put(genreName, genre);
                genreRepository.save(genre);
            }
        }
    }

    private MovieEntity parseMovie(CSVRecord record, HashMap<Long, String> movieToImdb, HashMap<String, GenreEntity> genreMap) {
        MovieEntity movie = new MovieEntity();
        movie.setFileId(Long.parseLong(record.get("movieId")));
        movie.setTitle(record.get("title"));
        movie.setImdbId(movieToImdb.get(movie.getFileId()));
        if (record.get("genres").isEmpty()) {
            return movie;
        }
        if (record.get("genres").equals("(no genres listed)")) {
            return movie;
        }
        String[] genreNames = record.get("genres").split("\\|");
        List<GenreEntity> genres = new ArrayList<>();
        for (String genreName : genreNames) {
            genres.add(genreMap.get(genreName));
        }
        movie.setGenres(genres);
        return movie;

    }


    private static HashMap<Long, String> getImdbForMovie(Iterable<CSVRecord> imdbIdRecords) {
        HashMap<Long, String> movieToImdb = new HashMap<>();
        for (CSVRecord record : imdbIdRecords) {
            movieToImdb.put(Long.parseLong(record.get("movieId")), record.get("imdbId"));
        }
        return movieToImdb;
    }
}
