package com.ll.blog.service;

import com.ll.blog.dao.TagRepository;
import com.ll.blog.entity.Tag;
import com.ll.blog.entity.Type;
import com.ll.blog.exception.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Override
    public Page<Tag> list(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag update(Tag tag) {
        Tag tag1 = get(tag.getId());
        if(tag1 == null) throw new NotFoundException("数据不存在");
        BeanUtils.copyProperties(tag,tag1);
        return tagRepository.save(tag);
    }

    @Override
    public Tag get(Long id) {
        return tagRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public List<Tag> findTopByBlogSize(int i) {
        Sort sort = Sort.by(Sort.Direction.DESC, "blogs.size");
        Pageable pageable = PageRequest.of(0, i, sort);
        return tagRepository.findTopByBlogSize(pageable);
    }
}
