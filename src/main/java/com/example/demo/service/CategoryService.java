package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    @Value("${admin.api.key}")
    private String ADMIN_API_KEY;

    private final CategoryRepository categoryRepository;

    private static final Pattern CATEGORY_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s]{3,50}$");

    @PostConstruct
    public void init() {
        if (!categoryRepository.existsByName("Default")) {
            Category defaultCategory = new Category();
            defaultCategory.setName("Default");
            categoryRepository.save(defaultCategory);
        }
    }

    public ResponseEntity<Void> createCategories(String token, Map<String, List<String>> requestBody) {
        if (!ADMIN_API_KEY.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> categories = requestBody.get("categories");

        if (categories == null || categories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        for (String name : categories) {
            if (!isValidCategoryName(name)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            if (categoryRepository.existsByName(name)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Category category = new Category();
            category.setName(name);

            categoryRepository.save(category);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private boolean isValidCategoryName(String name) {
        return CATEGORY_NAME_PATTERN.matcher(name).matches();
    }

    public ResponseEntity<List<String>> listCategories(String sessionCookie) {
        if (sessionCookie == null || !sessionCookie.contains("session=")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> categories = categoryRepository.findAll()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }
}