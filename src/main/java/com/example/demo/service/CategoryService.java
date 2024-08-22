package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.model.request.CreateCategoriesRequest;
import com.example.demo.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    @Value("${admin.api.key}")
    private String adminApiKey;

    private final CategoryRepository categoryRepository;
    private final JwtService jwtService;

    private final EntityManager entityManager;

    @PostConstruct
    public void init() {
        if (!categoryRepository.existsByName("Default")) {
            Category defaultCategory = new Category();
            defaultCategory.setName("Default");
            categoryRepository.save(defaultCategory);
        }
    }

    @Transactional
    public ResponseEntity<String> createCategories(String token, CreateCategoriesRequest request) {
        if (!token.equals(adminApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Set<String> categories = new HashSet<>(request.getCategories());
        if (categories.size() != request.getCategories().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicate categories in the payload");
        }

        if (categoryRepository.existsAnyByNameIn(categories)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("One or more category names already exist");
        }

        List<Category> currentBatch = new ArrayList<>();
        int batchSize = 20; // Adjust batch size as needed

        for (String name : categories) {
            Category category = new Category();
            category.setName(name);
            currentBatch.add(category);

            if (currentBatch.size() == batchSize) {
                categoryRepository.saveAll(currentBatch);
                entityManager.flush();
                entityManager.clear();
                currentBatch.clear();
            }
        }

        // Save any remaining categories in the batch
        if (!currentBatch.isEmpty()) {
            categoryRepository.saveAll(currentBatch);
            entityManager.flush();
            entityManager.clear();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<List<String>> listCategories(String sessionCookie) {
        if (!jwtService.isSessionValid(sessionCookie)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> categories = categoryRepository.findAll()
                .stream()
                .map(Category::getName)
                .toList();

        return ResponseEntity.ok(categories);
    }

    public ResponseEntity<String> deleteCategory(String token, String categoryName) {
        if (!token.equals(adminApiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (categoryName.equalsIgnoreCase("Default")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot delete the Default category");
        }

        Category category = categoryRepository.findByName(categoryName).orElse(null);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Category not found: \"%s\"", categoryName));
        }

        categoryRepository.delete(category);

        return ResponseEntity.ok().build();
    }
}