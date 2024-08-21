package com.example.demo.controller;

import com.example.demo.service.CsvImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/csv")
@RequiredArgsConstructor
public class CsvImportController {

    private final CsvImportService csvImportService;

    @PostMapping
    public ResponseEntity<Void> importUsersViaCsv(@RequestHeader(value = "Token", defaultValue = "") String token,
                                                  @RequestBody String csvData) {
        return csvImportService.importUsersViaCsv(token, csvData);
    }
}