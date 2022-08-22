package com.ll.blog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.blog.dto.BlogViewDto;
import com.ll.blog.entity.Blog;
import com.ll.blog.entity.Tag;
import com.ll.blog.entity.Type;
import com.ll.blog.service.BlogService;
import com.ll.blog.service.TagService;
import com.ll.blog.service.TypeService;
import com.ll.blog.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    BlogService blogService;
    @Autowired
    TypeService typeService;
    @Autowired
    TagService tagService;
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping({"/","index"})
    public String index(@PageableDefault(sort="updateTime", direction = Sort.Direction.DESC) Pageable pageable, Model model){
        Page page = blogService.listPublished(pageable);
        List<Type> types = typeService.findTopByBlogSize(6);
        List<Tag> tags = tagService.findTopByBlogSize(6);
        List<Blog> recommendBlogs = blogService.listRecommentBlogTop(6);
        model.addAttribute("page", page);
        model.addAttribute("types", types);
        model.addAttribute("tags", tags);
        model.addAttribute("recommendBlogs", recommendBlogs);
        return "index";
    }

    @PostMapping("search")
    public String search(@RequestParam String query, @PageableDefault(sort="updateTime", direction = Sort.Direction.DESC) Pageable pageable, Model model){
        Page page = blogService.searchLikeTitleContent(query, pageable);
        model.addAttribute("page", page);
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("blog/{id}")
    public String blog(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception{
        if(id == null) return "redirect:/";
        Blog blog = blogService.getAndConvert(id);
        model.addAttribute("blog",blog);
        if(blog == null) return "redirect:/";
        String ipAddress = IpUtils.getIPAddress(request);
        if(redisTemplate.opsForValue().get("blogWait_"+ipAddress) == null){//这个ip查看文章，计阅读量2分钟cd
            redisTemplate.opsForValue().set("blogWait_"+ipAddress, 1, 120, TimeUnit.SECONDS);
            String key = "blogViews_"+ id;
            ObjectMapper objectMapper = new ObjectMapper();
            if(redisTemplate.opsForValue().get(key) == null){
                int views = blog.getViews() + 1;
                BlogViewDto blogViewDto = new BlogViewDto(views, views);
                redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(blogViewDto));
            }else {
                Object result = redisTemplate.opsForValue().get(key);
                BlogViewDto blogViewDto = objectMapper.convertValue(result, BlogViewDto.class);

            }
        }

        return "blog";
    }

    @GetMapping("blog/like/{id}")
    public String blogLike(){
        return null;
    }


}
