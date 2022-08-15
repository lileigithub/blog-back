package com.ll.blog.service;

import com.ll.blog.entity.Tag;
import com.ll.blog.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    List<Tag> findAll();

    List<Tag> findByIds(String ids);

    public Page<Tag> list(Pageable pageable);

    Tag save(Tag tag);

    Tag update(Tag tag);

    Tag get(Long id);

    void delete(Long id);

    List<Tag> findTopByBlogSize(int i);
}
