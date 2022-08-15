package com.ll.blog.service;

import com.ll.blog.dao.TypeRepository;
import com.ll.blog.entity.Type;
import com.ll.blog.exception.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService{

    @Autowired
    TypeRepository typeRepository;

    @Transactional
    @Override
    public Type save(Type type) {
        return typeRepository.save(type);
    }

    @Override
    public Type get(Long id) {
        return typeRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Type> list(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Type update(Type type) {
        Type type1 = get(type.getId());
        if(type1 == null) throw new NotFoundException("数据不存在");
        BeanUtils.copyProperties(type,type1);
        return typeRepository.save(type);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        typeRepository.deleteById(id);
    }

    @Override
    public List<Type> findAll() {
        return typeRepository.findAll();
    }

    @Override
    public List<Type> findTopByBlogSize(int i) {
        Sort sort = Sort.by(Sort.Direction.DESC, "blogs.size");
        Pageable pageable = PageRequest.of(0, i, sort);
        return typeRepository.findTopByBlogSize(pageable);
    }
}
