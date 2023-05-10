package com.example.fighting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class loginController {
    //SpringSecuroty登入後導向的位置
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
