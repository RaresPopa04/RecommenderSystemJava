package com.recommender.controller;

import com.recommender.api.UploadUsersApi;
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
public class UserImportController implements UploadUsersApi {

    private final DataImportService dataImportService;

    public UserImportController(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    @Override
    public ResponseEntity<UploadResponse> uploadUsersPost(MultipartFile users) {
        try (Reader in = new InputStreamReader(users.getInputStream())) {
            Iterable<CSVRecord> userRecords = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build().parse(in);

            UploadResponse uploadResponse = new UploadResponse();
            dataImportService.importUsers(userRecords, uploadResponse.getErrors());
            return ResponseEntity.ok(uploadResponse);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
