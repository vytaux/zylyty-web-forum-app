package com.example.demo.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateCategoriesRequest {
    @NotEmpty(message = "Please provide at least 1 category")
    private List<
            @NotBlank(message = "Category name is mandatory")
            @Size(min = 3, max = 200, message = "Category name must be between 3 and 200 characters")
                    String> categories;
}