package com.example.demo.controller;

import com.example.demo.model.Thread;
import com.example.demo.model.request.PostRequest;
import com.example.demo.model.request.ThreadRequest;
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
    public ResponseEntity<Void> createThread(@RequestHeader("Cookie") String sessionCookie,
                                             @Valid @RequestBody ThreadRequest threadRequest) {
        return threadService.createThread(sessionCookie, threadRequest);
    }

    @GetMapping
    public ResponseEntity<List<Thread>> listThreads(@RequestHeader("Cookie") String sessionCookie,
                                                    @RequestParam(required = false, defaultValue = "Default") List<String> categories,
                                                    @RequestParam(name = "newest_first", required = false, defaultValue = "true") boolean newestFirst,
                                                    @RequestParam(required = false, defaultValue = "1") int page,
                                                    @RequestParam(name = "page_size", required = false, defaultValue = "10") int pageSize) {
        return threadService.listThreads(sessionCookie, categories, newestFirst, page, pageSize);
    }

    @PostMapping("/post")
    public ResponseEntity<Void> addPosts(@RequestHeader("Cookie") String sessionCookie,
                                         @Valid @RequestBody PostRequest postRequest) {
        return threadService.addPosts(sessionCookie, postRequest);
    }

    @GetMapping("/post")
    public ResponseEntity<Thread> getPosts(@RequestHeader(value = "Cookie", defaultValue = "") String sessionCookie,
                                           @RequestParam("thread_id") Long threadId) {
        return threadService.getPosts(sessionCookie, threadId);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteThread(@RequestHeader(value = "Token", defaultValue = "") String adminApiKey,
                                             @RequestParam("id") Long threadId) {
        return threadService.deleteThread(threadId, adminApiKey);
    }
}