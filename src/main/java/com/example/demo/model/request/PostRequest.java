package com.example.demo.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    @NotNull(message = "Thread ID is mandatory")
    private Long threadId;

    @Valid
    private List<Post> posts;

    @Data
    public static class Post {
        @NotBlank(message = "Post text is mandatory")
        private String text;
    }
}