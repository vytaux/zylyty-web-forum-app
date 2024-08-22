package com.example.demo.controller;

import com.example.demo.model.request.PostRequest;
import com.example.demo.model.request.ThreadRequest;
import com.example.demo.model.response.GetPostsResponse;
import com.example.demo.service.ThreadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/thread")
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadService threadService;

    @PostMapping
    public ResponseEntity<String> createThread(@RequestHeader(value = "Cookie", defaultValue = "") String sessionCookie,
                                               @Valid @RequestBody ThreadRequest threadRequest) {
        return threadService.createThread(sessionCookie, threadRequest);
    }

    @GetMapping
    public ResponseEntity<?> listThreads(@RequestHeader("Cookie") String sessionCookie,
                                         @RequestParam List<String> categories,
                                         @RequestParam(name = "newest_first") boolean newestFirst,
                                         @RequestParam int page,
                                         @RequestParam(name = "page_size") int pageSize) {
        return threadService.listThreads(sessionCookie, categories, newestFirst, page, pageSize);
    }

    @PostMapping("/post")
    public ResponseEntity<Void> addPosts(@RequestHeader(value = "Cookie", defaultValue = "") String sessionCookie,
                                         @Valid @RequestBody PostRequest postRequest) {
        return threadService.addPosts(sessionCookie, postRequest);
    }

    @GetMapping("/post")
    public ResponseEntity<GetPostsResponse> getPosts(@RequestHeader(value = "Cookie", defaultValue = "") String sessionCookie,
                                                     @RequestParam("thread_id") Long threadId) {
        return threadService.getPosts(sessionCookie, threadId);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteThread(@RequestHeader(value = "Token", defaultValue = "") String adminApiKey,
                                             @RequestParam("id") Long threadId) {
        return threadService.deleteThread(threadId, adminApiKey);
    }
}