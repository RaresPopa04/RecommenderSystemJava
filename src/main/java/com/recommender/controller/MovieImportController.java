package com.recommender.controller;

import com.recommender.api.UploadMoviesApi;
import com.recommender.model.UploadResponse;
import com.recommender.service.DataImportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@RestController
public class MovieImportController implements UploadMoviesApi {

    private final DataImportService dataImportService;

    public MovieImportController(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @Override
    public ResponseEntity<UploadResponse> uploadMoviesPost(MultipartFile movieGenres, MultipartFile movieImdbIds) {
        try (Reader in = new InputStreamReader(movieGenres.getInputStream());
             Reader in2 = new InputStreamReader(movieImdbIds.getInputStream())) {
            Iterable<CSVRecord> movieGenresRecords = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build().parse(in);
            Iterable<CSVRecord> imdbIdRecords = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build().parse(in2);

            UploadResponse uploadResponse = new UploadResponse();
            dataImportService.importMovies(movieGenresRecords, imdbIdRecords, uploadResponse.getErrors());
            return ResponseEntity.ok(uploadResponse);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
