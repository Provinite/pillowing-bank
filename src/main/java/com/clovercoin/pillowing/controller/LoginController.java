package com.clovercoin.pillowing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login() {
        return "login/login";
    }

    @RequestMapping("/access-denied")
    @ResponseBody
    public String accessDenied() {
        return "errors/access-denied";
    }

    @RequestMapping("/")
    public String index() {
        return "redirect:/admin/";
    }
}
