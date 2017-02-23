package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String index() {
        return "login/login";
    }

    @RequestMapping("/access-denied")
    @ResponseBody
    public String accessDenied() {
        return "errors/access-denied";
    }
}
