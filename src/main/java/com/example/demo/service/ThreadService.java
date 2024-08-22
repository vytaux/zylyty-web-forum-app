package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.Post;
import com.example.demo.model.Thread;
import com.example.demo.model.User;
import com.example.demo.model.request.PostRequest;
import com.example.demo.model.request.ThreadRequest;
import com.example.demo.model.response.GetPostsResponse;
import com.example.demo.model.response.ListThreadsResponse;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ThreadRepository;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThreadService {

    private final UserRepository userRepository;
    @Value("${admin.api.key}")
    private String adminApiKey;

    private final ThreadRepository threadRepository;
    private final CategoryRepository categoryRepository;
    private final JwtService jwtService;

    public ResponseEntity<String> createThread(String sessionCookie, ThreadRequest request) {
        Claims payload = jwtService.getTokenPayload(sessionCookie);

        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Category category = categoryRepository.findByName(request.getCategory()).orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Category %s does not exist", request.getCategory()));
        }

        String authorName = payload.getSubject();

        User author = userRepository.findByUsername(authorName).orElse(null);
        if (author == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Thread thread = new Thread();
        thread.setCategory(category);
        thread.setTitle(request.getTitle());
        thread.setAuthor(author);

        Post openingPost = new Post();
        openingPost.setAuthor(author);
        openingPost.setText(request.getOpeningPost().getText());
        openingPost.setOpeningPost(true);
        openingPost.setThread(thread);

        thread.getPosts().add(openingPost);

        threadRepository.save(thread);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<?> listThreads(
            String sessionCookie,
            List<String> categories,
            boolean newestFirst,
            int page,
            int pageSize
    ) {
        if (!jwtService.isSessionValid(sessionCookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        for (String category : categories) {
            if (!categoryRepository.existsByName(category)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(String.format("Category \"%s\" does not exist", category));
            }
        }

        Sort sort = newestFirst ? Sort.by("id").descending() : Sort.by("id").ascending();
        PageRequest pageRequest = PageRequest.of(page, pageSize, sort);

        Page<Long> threadIdsPage = threadRepository.findThreadIdsByCategoriesPaged(categories, pageRequest);
        List<Thread> threads = threadRepository.findThreadsWithAssociationsByIds(threadIdsPage.getContent(), sort);

        List<ListThreadsResponse.ThreadDTO> threadDTOs = new ArrayList<>();
        for (Thread thread : threads) {
            ListThreadsResponse.ThreadDTO threadDTO = new ListThreadsResponse.ThreadDTO();
            threadDTO.setId(thread.getId());
            threadDTO.setCategory(thread.getCategory().getName());
            threadDTO.setTitle(thread.getTitle());
            threadDTO.setAuthor(thread.getAuthor().getUsername());
            threadDTO.setCreatedAt(thread.getCreatedAt());

            ListThreadsResponse.PostDTO openingPostResponse = new ListThreadsResponse.PostDTO();
            openingPostResponse.setText(thread.getPosts().get(0).getText());
            threadDTO.setOpeningPost(openingPostResponse);

            threadDTOs.add(threadDTO);
        }

        ListThreadsResponse response = new ListThreadsResponse();
        response.setThreads(threadDTOs);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Void> addPosts(String sessionCookie, PostRequest postRequest) {
        Claims claims = jwtService.getTokenPayload(sessionCookie);
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Thread thread = threadRepository.findById(postRequest.getThreadId()).orElse(null);
        if (thread == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String authorName = claims.getSubject();

        User author = userRepository.findByUsername(authorName).orElse(null);
        if (author == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        for (PostRequest.Post postRequestPost : postRequest.getPosts()) {
            Post post = new Post();
            post.setAuthor(author);
            post.setText(postRequestPost.getText());
            post.setThread(thread);
            thread.getPosts().add(post);
        }

        threadRepository.save(thread);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<GetPostsResponse> getPosts(String sessionCookie, Long threadId) {
        if (!jwtService.isSessionValid(sessionCookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Thread thread = threadRepository.findById(threadId).orElse(null);
        if (thread == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        GetPostsResponse response = new GetPostsResponse();
        response.setId(thread.getId());
        response.setCategory(thread.getCategory().getName());
        response.setTitle(thread.getTitle());
        response.setText(thread.getPosts().get(0).getText());
        response.setAuthor(thread.getAuthor().getUsername());
        response.setCreatedAt(thread.getCreatedAt());

        List<GetPostsResponse.PostDTO> postDTOs = new ArrayList<>();
        for (Post post : thread.getPosts()) {
            if (post.isOpeningPost()) {
                continue;
            }

            GetPostsResponse.PostDTO postDTO = new GetPostsResponse.PostDTO();
            postDTO.setAuthor(post.getAuthor().getUsername());
            postDTO.setText(post.getText());
            postDTO.setCreatedAt(post.getCreatedAt());
            postDTOs.add(postDTO);
        }

        response.setPosts(postDTOs);

        return ResponseEntity.ok(response);
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