package com.ll.blog.service;

import com.ll.blog.entity.Blog;
import com.ll.blog.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {
    Page list(Pageable pageable, BlogQuery blog);

    Page list(Pageable pageable);

    Blog save(Blog blog);

    Blog get(Long id);

    void delete(Long id);

    List<Blog> listRecommentBlogTop(int i);
}
