package com.ll.blog.service;

import com.ll.blog.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeService {
    Type save(Type type);
    Type get(Long id);
    Page<Type> list(Pageable pageable);
    Type update(Type type);
    void delete(Long id);

    List<Type> findAll();
}
