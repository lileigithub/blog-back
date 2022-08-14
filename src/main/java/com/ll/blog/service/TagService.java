package com.ll.blog.service;

import com.ll.blog.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> findAll();

    List<Tag> findByIds(String ids);
}
