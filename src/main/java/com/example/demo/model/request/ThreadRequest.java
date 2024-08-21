package com.example.demo.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ThreadRequest {
    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotBlank(message = "Thread title is mandatory")
    @Size(min = 3, max = 100, message = "Thread title must be between 3 and 100 characters")
    private String title;

    @Valid
    private OpeningPostRequest openingPost;

    @Data
    public static class OpeningPostRequest {
        @NotBlank(message = "Opening post text is mandatory")
        private String text;
    }
}