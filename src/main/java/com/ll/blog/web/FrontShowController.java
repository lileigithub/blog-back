package com.ll.blog.web;

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

/**
 * description
 * @author lilei
 * @date 2022/8/16 18:06
 */
@Controller
public class FrontShowController {
    @Autowired
    TypeService typeService;
    @Autowired
    BlogService blogService;
    @Autowired
    TagService tagService;

    @GetMapping("/types/{id}")
    public String types(@PageableDefault(sort="updateTime", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long id, Model model){
        List<Type> types = typeService.findTopByBlogSize(10000);
        if(id == -1){
            id = types.get(0).getId();
        }
        BlogQuery blogQuery = new BlogQuery();
        blogQuery.setTypeId(id);
        Page page = blogService.listPublished(pageable, blogQuery);
        model.addAttribute("page", page);
        model.addAttribute("types", types);
        model.addAttribute("activeTypeId", id);
        return "/types";
    }

    @GetMapping("/tags/{id}")
    public String tags(@PageableDefault(sort="updateTime", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long id, Model model){
        List<Tag> tags = tagService.findTopByBlogSize(10000);
        if(id == -1){
            id = tags.get(0).getId();
        }
        Page page = blogService.listPublishedByTagId(pageable, id);
        model.addAttribute("page", page);
        model.addAttribute("tags", tags);
        model.addAttribute("activeTagId", id);
        return "/tags";
    }
}
