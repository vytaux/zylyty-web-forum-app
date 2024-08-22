package com.example.demo.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ListThreadsResponse {
    private List<ThreadDTO> threads;

    @Data
    public static class ThreadDTO {
        private Long id;
        private String category;
        private String title;
        private String author;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private LocalDateTime createdAt;
        private PostDTO openingPost;
    }

    @Data
    public static class PostDTO {
        private String text;
    }
}