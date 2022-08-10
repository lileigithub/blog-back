package com.ll.blog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class IndexController {


    public String index(){
        //int i = 9/0;
        return "index";
    }
}
