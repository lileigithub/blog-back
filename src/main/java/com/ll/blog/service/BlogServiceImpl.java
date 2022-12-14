package com.ll.blog.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.blog.dao.BlogRepository;
import com.ll.blog.dao.TagRepository;
import com.ll.blog.dto.BlogCacheDto;
import com.ll.blog.entity.Blog;
import com.ll.blog.entity.Tag;
import com.ll.blog.entity.Type;
import com.ll.blog.exception.NotFoundException;
import com.ll.blog.util.Constants;
import com.ll.blog.util.MarkdownUtils;
import com.ll.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService{
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public Page listPublished(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
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
    }

    @Override
    public Page listPublished(Pageable pageable) {
        return blogRepository.listPublish(pageable);
    }

    @Transactional
    @Override
    public Blog save(Blog blog) {
        if(blog.getId() == null){
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        }else{
            Blog exitsBlog = blogRepository.findById(blog.getId()).orElse(null);
            if(exitsBlog == null) throw new NotFoundException("????????????????????????");
            blog.setUpdateTime(new Date());
            BeanUtils.copyProperties(blog, exitsBlog);
            blog = exitsBlog;
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
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);
        if(blog != null){
            List<Tag> tags = blog.getTags();
            if(!CollectionUtils.isEmpty(tags)){
                String collect = tags.stream().map(s -> String.valueOf(s.getId())).collect(Collectors.joining(","));
                blog.setTagIds(collect);
            }
        }
        String content = blog.getContent();
        String html = MarkdownUtils.markdownToHtmlExtensions(content);
        blog.setContent(html);
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

    @Override
    public Page searchLikeTitleContent(String query, Pageable pageable) {
        return blogRepository.searchLikeTitleContent(query, pageable);
    }

    @Override
    public Page listPublishedByTagId(Pageable pageable, Long id) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join join = root.join("tags");
                return cb.equal(join.get("id"), id);
            }
        }, pageable);
    }

    @Override
    public Map<String, List<Blog>> archives() {
        List<String> years = blogRepository.archivesYears();
        Map<String, List<Blog>> map = new LinkedHashMap<>();
        if(!CollectionUtils.isEmpty(years)){
            for (String year : years) {
                List<Blog> blogs = blogRepository.selectByYear(year);
                map.put(year, blogs);
            }
        }
        return map;
    }

    @Override
    public Long count(Boolean published) {
        return blogRepository.count(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (published != null) {
                    predicates.add(cb.equal(root.<Boolean>get("published"), published));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        });
    }

    @Override
    public void blogLike(Long id, String ipAddress) {
        if(blogRepository.existsById(id)){
            /*HyperLogLogOperations<String, String> logLogOperations = stringRedisTemplate.opsForHyperLogLog();
            logLogOperations.add(Constants.BLOG_LIKES_KEY+id, ipAddress);*/
        }
    }

    /**
     * ??????+1 ????????????set add
     * @param id
     * @param flag
     * @param ipAddress
     */
    @Override
    public Blog saveLikesCache(Long id, Integer flag, String ipAddress) {
        ObjectMapper objectMapper = new ObjectMapper();
        Object getObject = redisTemplate.opsForValue().get(Constants.BLOG_CACHE_KEY + id);
        BlogCacheDto blogCacheDto = objectMapper.convertValue(getObject, new TypeReference<BlogCacheDto>() {
        });
        Integer likes = 0;
        if(flag == 1){//??????
            //???set
            Long add = redisTemplate.opsForSet().add(Constants.USER_BLOG_LIKES_SET + ipAddress, id);
            if(add != 0)
            {
                //??????
                likes = blogCacheDto.getLikes() == null ? 1 : (blogCacheDto.getLikes() +1);
                blogCacheDto.setLikes(likes);
                blogCacheDto.setChanged(true);
            }
        } else if(flag == 0){
            //????????????
            Long remove = redisTemplate.opsForSet().remove(Constants.USER_BLOG_LIKES_SET + ipAddress, id);
            if(remove != 0){
                likes = (blogCacheDto.getLikes() == null || blogCacheDto.getLikes() == 0) ? 0 : (blogCacheDto.getLikes() -1);
                blogCacheDto.setLikes(likes);
                blogCacheDto.setChanged(true);
            }
        }
        
        redisTemplate.opsForValue().set(Constants.BLOG_CACHE_KEY + id, blogCacheDto);
        Blog blog = new Blog();
        blog.setLikes(blogCacheDto.getLikes());
        return blog;
    }

    @Override
    public void saveBlogCache(Long id, BlogCacheDto blogCacheDto) {
        blogRepository.saveBlogCache(id, blogCacheDto);
    }
}
