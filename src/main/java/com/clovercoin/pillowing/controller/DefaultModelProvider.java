package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.service.ConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class DefaultModelProvider {
    private ConstantService constantService;

    @Autowired
    public DefaultModelProvider(ConstantService constantService) {
        Assert.notNull(constantService, "The DefaultModelProvider requires a ConstantService.");

        this.constantService = constantService;
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

}
