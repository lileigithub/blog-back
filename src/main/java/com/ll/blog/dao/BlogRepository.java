package com.ll.blog.dao;

import com.ll.blog.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {
    @Query("select b from Blog b where b.recommend = true ")
    List<Blog> listRecommentBlogTop(Pageable pageable);

    @Query("select b from Blog b where b.published = true ")
    Page listPublish(Pageable pageable);

    @Query("select b from Blog b where b.published = true and (b.title like CONCAT('%',?1,'%') or b.content like CONCAT('%',?1,'%'))")
    Page searchLikeTitleContent(String query, Pageable pageable);
}
