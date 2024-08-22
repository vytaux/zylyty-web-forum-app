package com.example.demo.repository;

import com.example.demo.model.Thread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    @Query("SELECT t.id FROM Thread t " +
            "JOIN t.category c " +
            "WHERE c.name IN :categories")
    Page<Long> findThreadIdsByCategoriesPaged(@Param("categories") List<String> categories, Pageable pageable);

    @Query("SELECT t FROM Thread t " +
            "LEFT JOIN FETCH t.category c " +
            "LEFT JOIN FETCH t.posts p " +
            "LEFT JOIN FETCH p.author " +
            "WHERE t.id IN :ids AND p.isOpeningPost = true")
    List<Thread> findThreadsWithAssociationsByIds(@Param("ids") List<Long> ids, Sort sort);

    @Query("SELECT t FROM Thread t " +
            "LEFT JOIN FETCH t.category c " +
            "LEFT JOIN FETCH t.posts p " +
            "WHERE LOWER(t.title) LIKE %:searchText% OR LOWER(p.text) LIKE %:searchText%")
    List<Thread> searchByTitleOrPostsContent(@Param("searchText") String searchText);
}