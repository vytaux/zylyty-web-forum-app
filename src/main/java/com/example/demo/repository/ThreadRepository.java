package com.example.demo.repository;

import com.example.demo.model.Thread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    Page<Thread> findByCategoryNameInOrderByCreatedAtDesc(
            List<String> categories,
            Pageable pageable
    );

    @Query("SELECT t FROM Thread t LEFT JOIN t.posts p WHERE LOWER(t.title) LIKE %:searchText% OR LOWER(p.text) LIKE %:searchText%")
    List<Thread> searchByTitleOrPostsContent(@Param("searchText") String searchText);
}