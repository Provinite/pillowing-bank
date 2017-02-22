package com.clovercoin.pillowing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
    @RequestMapping("abc")
    public String index() {
        return "login/index";
    }
}
