package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.service.ConstantService;
import com.clovercoin.pillowing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class DefaultModelProvider {
    private ConstantService constantService;
    private UserService userService;


    @Autowired
    public DefaultModelProvider(ConstantService constantService, UserService userService) {
        Assert.notNull(constantService, "The DefaultModelProvider requires a ConstantService.");
        Assert.notNull(userService, "The DefaultModelProvider requires a UserService.");
        this.constantService = constantService;
        this.userService = userService;
    }

    @ModelAttribute
    public void addKeyNamesToModel(Model model) {
        constantService
                .getAllWithPrefix("MODEL_KEY_")
                .entrySet()
                .forEach(e -> {
                    model.addAttribute(e.getKey().replace("MODEL_KEY_", ""), e.getValue());
                });
    }

    @ModelAttribute
    public void setDefaultModelAttributes(Model model) {
        model.addAttribute(constantService.getString("MODEL_KEY_STATUS"), constantService.getString("STATUS_NONE"));
        model.addAttribute(constantService.getString("MODEL_KEY_ACTION"), constantService.getString("ACTION_NONE"));
    }

    @ModelAttribute
    public void addRolesToModel(Model model) {
        User user = userService.getCurrentUser();
        boolean isAdmin;
        boolean isMod;
        if (user == null) {
            isAdmin = false;
            isMod = false;
        } else {
            isAdmin = userService.userHasRole(user, constantService.getString("ROLE_ADMIN"));
            isMod = userService.userHasRole(user, constantService.getString("ROLE_MOD"));
        }
        model.addAttribute(constantService.getString("MODEL_KEY_IS_ADMIN"), isAdmin);
        model.addAttribute(constantService.getString("MODEL_KEY_IS_MOD"), isMod);
    }

}
