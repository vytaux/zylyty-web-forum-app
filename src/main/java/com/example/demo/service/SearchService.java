package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.model.Thread;
import com.example.demo.repository.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ThreadRepository threadRepository;

    public ResponseEntity<Map<Long, List<String>>> searchThreads(String sessionCookie, String searchText) {
        if (sessionCookie == null || !sessionCookie.contains("session=")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Thread> threads = threadRepository.searchByTitleOrPostsContent(searchText.toLowerCase());

        Map<Long, List<String>> searchResults = new HashMap<>();
        for (Thread thread : threads) {
            List<String> snippets = new ArrayList<>();
            String lowerCaseSearchText = searchText.toLowerCase();

            // Search in thread title
            if (thread.getTitle().toLowerCase().contains(lowerCaseSearchText)) {
                snippets.add(getSnippet(thread.getTitle(), lowerCaseSearchText));
            }

            // Search in posts
            for (Post post : thread.getPosts()) {
                if (post.getText().toLowerCase().contains(lowerCaseSearchText)) {
                    snippets.add(getSnippet(post.getText(), lowerCaseSearchText));
                }
            }

            if (!snippets.isEmpty()) {
                searchResults.put(thread.getId(), snippets);
            }
        }

        return ResponseEntity.ok(searchResults);
    }

    private String getSnippet(String text, String searchText) {
        String lowerCaseText = text.toLowerCase();
        String lowerCaseSearchText = searchText.toLowerCase();
        int index = lowerCaseText.indexOf(lowerCaseSearchText);

        if (index == -1) {
            return "";
        }

        String[] words = text.split("\\s+");
        int wordIndex = 0;
        int charCount = 0;

        // Find the word index of the search text
        for (int i = 0; i < words.length; i++) {
            charCount += words[i].length() + 1; // +1 for the space
            if (charCount > index) {
                wordIndex = i;
                break;
            }
        }

        int start = Math.max(0, wordIndex - 3);
        int end = Math.min(words.length, wordIndex + 4); // +4 to include the search text word

        StringBuilder snippet = new StringBuilder("...");
        for (int i = start; i < end; i++) {
            snippet.append(words[i]).append(" ");
        }
        snippet.append("...");

        return snippet.toString().trim();
    }
}
