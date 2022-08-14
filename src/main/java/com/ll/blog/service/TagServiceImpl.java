package com.ll.blog.service;

import com.ll.blog.dao.TagRepository;
import com.ll.blog.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagRepository tagRepository;

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> findByIds(String ids) {
        if(!StringUtils.isEmptyOrWhitespace(ids)){
            String[] split = ids.split(",");
            List<Long> longIds = new ArrayList<>();
            for (String s : split) {
                longIds.add(Long.parseLong(s));
            }
            return tagRepository.findAllById(longIds);
        }
        return null;
    }
}
