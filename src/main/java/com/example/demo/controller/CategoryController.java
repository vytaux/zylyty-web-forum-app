package com.example.demo.controller;

import com.example.demo.model.request.CreateCategoriesRequest;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<String> createCategories(@RequestHeader(value = "Token", defaultValue = "") String token,
                                                   @Valid @RequestBody CreateCategoriesRequest request) {
        return categoryService.createCategories(token, request);
    }

    @GetMapping
    public ResponseEntity<List<String>> listCategories(
            @RequestHeader(value = "Cookie", defaultValue = "") String sessionCookie
    ) {
        return categoryService.listCategories(sessionCookie);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCategory(@RequestHeader(value = "Token", defaultValue = "") String token,
                                                 @RequestParam("category") String categoryName) {
        return categoryService.deleteCategory(token, categoryName);
    }
}