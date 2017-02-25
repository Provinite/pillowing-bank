package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.constant.Action;
import com.clovercoin.pillowing.constant.Status;
import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.service.ClientService;
import com.clovercoin.pillowing.service.ConstantService;
import com.clovercoin.pillowing.service.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private ControllerHelper controllerHelper;

    @Autowired
    private ConstantService constantService;

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
    public String index(Model model) {
        model.addAttribute("client", new Client());
        controllerHelper.setAction(model, Action.ADD_SUBMIT);
        controllerHelper.setStatus(model, Status.SUCCESS);
        return "admin/add-client";
    }
}
