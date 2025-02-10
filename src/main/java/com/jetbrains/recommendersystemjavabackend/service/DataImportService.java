package com.jetbrains.recommendersystemjavabackend.service;


import com.jetbrains.recommendersystemjavabackend.model.Error;
import com.jetbrains.recommendersystemjavabackend.model.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class DataImportService {

    private final MovieService movieService;

    public DataImportService(MovieService movieService) {
        this.movieService = movieService;
    }

    public void importMovies(Iterable<CSVRecord> movieGenreRecords, Iterable<CSVRecord> imdbIdRecords, List<Error> errors) {
        HashMap<Long, String> movieToImdb = getImdbForMovie(imdbIdRecords);
        for (CSVRecord record : movieGenreRecords) {
            try{
                movieService.saveRecord(record, movieToImdb, errors);
            }
            catch (Exception e){
                errors.add(new Error().message(e.getMessage()).id(Integer.valueOf(record.get("movieId"))));
            }
        }
    }


    private static HashMap<Long, String> getImdbForMovie(Iterable<CSVRecord> imdbIdRecords) {
        HashMap<Long, String> movieToImdb = new HashMap<>();
        for (CSVRecord record : imdbIdRecords) {
            movieToImdb.put(Long.parseLong(record.get("movieId")), record.get("imdbId"));
        }
        return movieToImdb;
    }
}
