package com.example.demo.controller;

import com.example.demo.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Void> createCategories(@RequestHeader("Token") String token,
                                                 @RequestBody Map<String, List<String>> requestBody) {
        return categoryService.createCategories(token, requestBody);
    }

    @GetMapping
    public ResponseEntity<List<String>> listCategories(HttpServletRequest request) {
        String sessionCookie = request.getHeader("Cookie");
        return categoryService.listCategories(sessionCookie);
    }

    @DeleteMapping("/categories")
    public ResponseEntity<String> deleteCategory(@RequestHeader("Token") String token,
                                                 @RequestParam("category") String categoryName) {
        return categoryService.deleteCategory(token, categoryName);
    }
}