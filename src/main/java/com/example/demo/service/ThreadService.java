package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.Post;
import com.example.demo.model.Thread;
import com.example.demo.model.request.PostRequest;
import com.example.demo.model.request.ThreadRequest;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ThreadRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThreadService {

    @Value("${admin.api.key}")
    private String adminApiKey;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static SecretKey SECRET_KEY;

    @PostConstruct
    public void init() {
        SECRET_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private final ThreadRepository threadRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity<Void> createThread(String sessionCookie, ThreadRequest threadRequest) {
        if (sessionCookie == null || !sessionCookie.contains("session=")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = sessionCookie.replace("session=", "");
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String author = claims.getSubject();

        Category category = categoryRepository.findByName(threadRequest.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Thread thread = new Thread();
        thread.setCategory(category);
        thread.setTitle(threadRequest.getTitle());
        thread.setAuthor(author);
        thread.setCreatedAt(LocalDateTime.now());

        Thread.OpeningPost openingPost = new Thread.OpeningPost();
        openingPost.setText(threadRequest.getOpeningPost().getText());
        thread.setOpeningPost(openingPost);

        threadRepository.save(thread);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<List<Thread>> listThreads(
            String sessionCookie,
            List<String> categories,
            boolean newestFirst,
            int page,
            int pageSize
    ) {
        if (sessionCookie == null || !sessionCookie.contains("session=")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Sort sort = newestFirst ? Sort.by("createdAt").descending() : Sort.by("createdAt").ascending();
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, sort);

        List<Thread> threads = threadRepository
                .findByCategoryNameInOrderByCreatedAtDesc(categories, pageRequest)
                .getContent();

        return ResponseEntity.ok(threads);
    }

    public ResponseEntity<Void> addPosts(String sessionCookie, PostRequest postRequest) {
        if (sessionCookie == null || !sessionCookie.contains("session=")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = sessionCookie.replace("session=", "");
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String author = claims.getSubject();

        Thread thread = threadRepository.findById(postRequest.getThreadId())
                .orElseThrow(() -> new IllegalArgumentException("Thread not found"));

        for (PostRequest.Post postRequestPost : postRequest.getPosts()) {
            Post post = new Post();
            post.setAuthor(author);
            post.setText(postRequestPost.getText());
            post.setCreatedAt(LocalDateTime.now());
            post.setThread(thread);
            thread.getPosts().add(post);
        }

        threadRepository.save(thread);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<Thread> getPosts(String sessionCookie, Long threadId) {
        if (sessionCookie == null || !sessionCookie.contains("session=")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found"));

        return ResponseEntity.ok(thread);
    }

    public ResponseEntity<Void> deleteThread(Long threadId, String adminApiKey) {
        if (!adminApiKey.equals(this.adminApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!threadRepository.existsById(threadId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        threadRepository.deleteById(threadId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}