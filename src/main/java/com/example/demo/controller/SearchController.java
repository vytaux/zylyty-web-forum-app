package com.example.demo.controller;

import com.example.demo.model.response.SearchResponse;
import com.example.demo.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<SearchResponse> searchThreads(@RequestHeader(value = "Cookie", defaultValue = "") String sessionCookie,
                                                        @RequestParam("text") String searchText) {
        return searchService.searchThreads(sessionCookie, searchText);
    }
}
