package com.ll.blog.dao;

import com.ll.blog.entity.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type, Long> {
    @Query("select t from Type t")
    List<Type> findTopByBlogSize(Pageable pageable);
}
