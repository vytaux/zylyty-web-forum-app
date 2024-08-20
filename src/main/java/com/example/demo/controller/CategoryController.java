package com.example.demo.controller;

import com.example.demo.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
}