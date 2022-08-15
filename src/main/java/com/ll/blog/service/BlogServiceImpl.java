package com.ll.blog.service;

import com.ll.blog.dao.BlogRepository;
import com.ll.blog.dao.TagRepository;
import com.ll.blog.entity.Blog;
import com.ll.blog.entity.Tag;
import com.ll.blog.entity.Type;
import com.ll.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService{
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    TagRepository tagRepository;
    @Override
    public Page list(Pageable pageable, BlogQuery blog) {
        Page<Blog> all = blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmptyOrWhitespace(blog.getTitle())) {
                    predicates.add(cb.like(root.<String>get("title"), "%" + blog.getTitle() + "%"));
                }
                if (blog.getTypeId() != null) {
                    predicates.add(cb.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }
                if (blog.isRecommend()) {
                    predicates.add(cb.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        }, pageable);
        return all;
    }

    @Override
    public Page list(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Blog save(Blog blog) {
        if(blog.getId() == null){
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        }else{
            //TODO 更新有问题，需要改 参考P36
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Override
    public Blog get(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);
        if(blog != null){
            List<Tag> tags = blog.getTags();
            if(!CollectionUtils.isEmpty(tags)){
                String collect = tags.stream().map(s -> String.valueOf(s.getId())).collect(Collectors.joining(","));
                blog.setTagIds(collect);
            }
        }
        return blog;
    }

    @Override
    public void delete(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public List<Blog> listRecommentBlogTop(int i) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, i, sort);
        return blogRepository.listRecommentBlogTop(pageable);
    }
}
