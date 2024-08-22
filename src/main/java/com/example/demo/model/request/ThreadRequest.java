package com.example.demo.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ThreadRequest {

    @NotBlank(message = "Category is required")
    @Size(min = 3, max = 200, message = "Category must be between 3 and 200 characters")
    private String category;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Valid
    private OpeningPost openingPost;

    @Data
    public static class OpeningPost {
        @NotBlank(message = "Text is required")
        private String text;
    }
}