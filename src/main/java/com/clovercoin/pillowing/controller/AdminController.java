package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.constant.Action;
import com.clovercoin.pillowing.constant.ErrorCode;
import com.clovercoin.pillowing.constant.Status;
import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.service.ClientService;
import com.clovercoin.pillowing.service.ConstantService;
import com.clovercoin.pillowing.service.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ControllerHelper controllerHelper;

    @Autowired
    private ConstantService constantService;

    @RequestMapping("/")
    public String index() {
        return "admin/index";
    }

    @RequestMapping(value = "/client", method = RequestMethod.GET)
    public String client(Model model) {
        model.addAttribute("client", new Client());

        return "admin/add-client";
    }

    @RequestMapping(value = "/client", method = RequestMethod.POST)
    public String client(Client client, Model model) {
        controllerHelper.setAction(model, Action.ADD_SUBMIT);
        model.addAttribute("client_name", client.getName());

        try {
            clientService.saveClient(client);
            client = new Client();
            controllerHelper.setStatus(model, Status.SUCCESS);
        } catch (DuplicateKeyException dke) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.DUPLICATE_KEY);
        }

        model.addAttribute("client", client);
        return "admin/add-client";
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String user(Model model) {
        model.addAttribute("user", new User());

        return "admin/add-user";
    }
}
