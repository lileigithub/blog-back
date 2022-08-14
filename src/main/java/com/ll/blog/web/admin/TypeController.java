package com.ll.blog.web.admin;

import com.ll.blog.entity.Type;
import com.ll.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("admin")
public class TypeController {
    @Autowired
    TypeService typeService;

    /**
     * 跳转到列表
     * @param pageable
     * @param model
     * @return
     */
    @GetMapping("types")
    public String types(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, Model model){
        Page<Type> list = typeService.list(pageable);
        int number = list.getNumber();
        model.addAttribute("page", list);
        return "admin/types";
    }

    /**
     * 跳转到新增
     * @param model
     * @return
     */
    @GetMapping("types/input")
    public String input(Model model){
        model.addAttribute("type", new Type());
        return "admin/types-input";
    }

    /**
     * 新增和修改
     * @param type
     * @param redirectAttributes
     * @param bindingResult
     * @return
     */
    @PostMapping("types")
    public String post(@Valid Type type, RedirectAttributes redirectAttributes, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "admin/types-input";
        }
        Type save;
        if(type.getId() == null){
            save = typeService.save(type);
        }else {
            save = typeService.update(type);
        }

        if(save == null){
            redirectAttributes.addFlashAttribute("message","操作失败");
        }else {
            redirectAttributes.addFlashAttribute("message","操作成功");
        }
        return "redirect:/admin/types";
    }

    /**
     * 跳转到编辑页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/types/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("type", typeService.get(id));
        return "admin/types-input";
    }

    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id){
        typeService.delete(id);
        return "redirect:/admin/types";
    }

}
