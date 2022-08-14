package com.ll.blog.service;

import com.ll.blog.entity.Blog;
import com.ll.blog.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    Page list(Pageable pageable, BlogQuery blog);

    Blog save(Blog blog);
}
