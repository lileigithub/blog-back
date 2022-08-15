package com.ll.blog.web.admin;

import com.ll.blog.entity.Tag;
import com.ll.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * description
 * @author lilei
 * @date 2022/8/15 17:33
 */
@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    TagService tagService;

    @GetMapping("/tags")
    public String types(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, Model model){
        model.addAttribute("page", tagService.list(pageable));
        return "/admin/tags";
    }

    /**
     * 跳转到新增
     * @param model
     * @return
     */
    @GetMapping("tags/input")
    public String input(Model model){
        model.addAttribute("tag", new Tag());
        return "admin/tags-input";
    }

    /**
     * 新增和修改
     * @param redirectAttributes
     * @param bindingResult
     * @return
     */
    @PostMapping("tags")
    public String post(@Valid Tag tag, RedirectAttributes redirectAttributes, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "admin/tags-input";
        }
        Tag save;
        if(tag.getId() == null){
            save = tagService.save(tag);
        }else {
            save = tagService.update(tag);
        }
        if(save == null){
            redirectAttributes.addFlashAttribute("message","操作失败");
        }else {
            redirectAttributes.addFlashAttribute("message","操作成功");
        }
        return "redirect:/admin/tags";
    }

    /**
     * 跳转到编辑页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("tag", tagService.get(id));
        return "admin/tags-input";
    }

    @GetMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        tagService.delete(id);
        redirectAttributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/tags";
    }
}
