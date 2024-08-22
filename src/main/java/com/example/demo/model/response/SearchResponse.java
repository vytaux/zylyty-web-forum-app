package com.example.demo.model.response;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SearchResponse {
    Map<Long, List<String>> searchResults = new HashMap<>();
}
