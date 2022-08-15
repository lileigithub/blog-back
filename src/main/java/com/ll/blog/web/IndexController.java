package com.ll.blog.web;

import com.ll.blog.entity.Blog;
import com.ll.blog.entity.Tag;
import com.ll.blog.entity.Type;
import com.ll.blog.service.BlogService;
import com.ll.blog.service.TagService;
import com.ll.blog.service.TypeService;
import com.ll.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    BlogService blogService;
    @Autowired
    TypeService typeService;
    @Autowired
    TagService tagService;

    @GetMapping({"/","index"})
    public String index(@PageableDefault(sort="updateTime", direction = Sort.Direction.DESC) Pageable pageable, Model model){
        Page list = blogService.list(pageable);
        List<Type> types = typeService.findTopByBlogSize(6);
        List<Tag> tags = tagService.findTopByBlogSize(6);
        List<Blog> recommendBlogs = blogService.listRecommentBlogTop(6);
        model.addAttribute("page", list);
        model.addAttribute("types", types);
        model.addAttribute("tags", tags);
        model.addAttribute("recommendBlogs", recommendBlogs);
        return "index";
    }


}
