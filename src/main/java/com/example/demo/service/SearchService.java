package com.example.demo.service;

import com.example.demo.model.Post;
import com.example.demo.model.Thread;
import com.example.demo.model.response.SearchResponse;
import com.example.demo.repository.ThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ThreadRepository threadRepository;
    private final JwtService jwtService;

    public ResponseEntity<SearchResponse> searchThreads(String sessionCookie, String searchText) {
        if (!jwtService.isSessionValid(sessionCookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Thread> threads = threadRepository.searchByTitleOrPostsContent(searchText.toLowerCase());

        SearchResponse searchResponse = new SearchResponse();
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
                searchResponse.getSearchResults().put(thread.getId(), snippets);
            }
        }

        return ResponseEntity.ok(searchResponse);
    }

    private String getSnippet(String text, String searchText) {
        String lowerCaseText = text.toLowerCase();
        String lowerCaseSearchText = searchText.toLowerCase();
        int index = lowerCaseText.indexOf(lowerCaseSearchText);

        // 0..index
        int posStart = findPos3WordsBefore(index, text);
        // index+searchText.length..text.length
        int posEnd = findPos3WordsAfter(index, text, searchText);

        String snippet = text.substring(posStart, posEnd);
        if (posStart > 0) {
            snippet = "..." + snippet;
        }
        if (posEnd < text.length()) {
            snippet = snippet + "...";
        }

        return snippet;
    }

    private int findPos3WordsBefore(int index, String text) {
        int wordsFound = 0;
        boolean inWord = false;

        for (int i = index-1; i > 0; i--) {
            if (text.charAt(i) == ' ') {
                if (inWord) {
                    wordsFound++;
                    inWord = false;
                }
            } else {
                inWord = true;
            }

            if (wordsFound == 3) {
                return i+1;
            }
        }

        return 0;
    }

    private int findPos3WordsAfter(int index, String text, String searchText) {
        int wordsFound = 0;
        boolean inWord = false;

        int nextWhitespace = text.indexOf(' ', index + searchText.length());
        for (int i = nextWhitespace; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                if (inWord) {
                    wordsFound++;
                    inWord = false;
                }
            } else {
                inWord = true;
            }

            if (wordsFound == 3) {
                return i;
            }
        }

        return text.length();
    }
}
