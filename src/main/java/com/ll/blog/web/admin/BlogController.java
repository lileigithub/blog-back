package com.ll.blog.web.admin;

import com.ll.blog.entity.Blog;
import com.ll.blog.entity.Type;
import com.ll.blog.entity.User;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class BlogController {
    @Autowired
    BlogService blogService;
    @Autowired
    TypeService typeService;

    @Autowired
    TagService tagService;

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(sort="updateTime", direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog, Model model){
        Page list = blogService.list(pageable, blog);
        model.addAttribute("page", list);
        List<Type> all = typeService.findAll();
        model.addAttribute("types", all);
        return "/admin/blogs";
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(sort="updateTime", direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog, Model model){
        model.addAttribute("page", blogService.list(pageable, blog));
        return "/admin/blogs :: blogList";
    }

    /**
     * 跳转到新增页面
     * @param model
     * @return
     */
    @GetMapping("/blogs/input")
    public String input(Model model){
        model.addAttribute("blog", new Blog());
        model.addAttribute("types", typeService.findAll());
        model.addAttribute("tags", tagService.findAll());
        return "/admin/blogs-input";
    }

    @PostMapping("/blogs")
    public String postInput(Blog blog, HttpSession session, RedirectAttributes redirectAttributes){
        User user = (User) session.getAttribute("user");
        blog.setType(typeService.get(blog.getType().getId()));
        blog.setTags(tagService.findByIds(blog.getTagIds()));
        Blog blog1 = blogService.save(blog);
        blog1.setUser(user);
        if(blog1 == null){
            redirectAttributes.addFlashAttribute("message","操作失败");
        }else {
            redirectAttributes.addFlashAttribute("message","操作成功");
        }
        return "redirect:/admin/blogs";
    }
}