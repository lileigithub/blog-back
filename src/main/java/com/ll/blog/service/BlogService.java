package com.ll.blog.service;

import com.ll.blog.entity.Blog;
import com.ll.blog.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BlogService {
    Page listPublished(Pageable pageable, BlogQuery blog);

    Page listPublished(Pageable pageable);

    Blog save(Blog blog);

    Blog get(Long id);

    Blog getAndConvert(Long id);

    void delete(Long id);

    List<Blog> listRecommentBlogTop(int i);

    Page searchLikeTitleContent(String query, Pageable pageable);

    Page listPublishedByTagId(Pageable pageable, Long id);

    Map<String, List<Blog>> archives();

    Long count(Boolean published);
}
