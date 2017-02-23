package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.entity.Client;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @RequestMapping("/")
    public String index() {
        return "admin/index";
    }

    @RequestMapping("/client")
    public String client(Model model) {
        model.addAttribute("client", new Client());
        return "admin/add-client";
    }
}
