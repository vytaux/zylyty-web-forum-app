package com.example.demo.controller;

import com.example.demo.service.SearchService;
import com.example.demo.service.ThreadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<Map<Long, List<String>>> searchThreads(@RequestHeader("Cookie") String sessionCookie,
                                                                 @RequestParam("text") String searchText) {
        if (sessionCookie == null || !sessionCookie.contains("session=")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return searchService.searchThreads(sessionCookie, searchText);
    }
}
