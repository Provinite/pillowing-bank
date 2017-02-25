package com.clovercoin.pillowing.service;

import com.clovercoin.pillowing.constant.Action;
import com.clovercoin.pillowing.constant.ErrorCode;
import com.clovercoin.pillowing.constant.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.Assert;

@Service
public class ControllerHelper {
    private ConstantService constantService;

    @Autowired
    public ControllerHelper(ConstantService constantService) {
        Assert.notNull(constantService, "The ControllerHelper requires a ConstantService.");

        this.constantService = constantService;
    }

    public void setStatus(Model model, Status status) {
        model.addAttribute(
                constantService.getString("MODEL_KEY_STATUS"),
                constantService.get("STATUS_" + status.name()));
    }

    public void setAction(Model model, Action action) {
        model.addAttribute(
                constantService.getString("MODEL_KEY_ACTION"),
                constantService.get("ACTION_" + action.name()));
    }

    public void setError(Model model, ErrorCode errorCode) {
        model.addAttribute(
                constantService.getString("MODEL_KEY_ERROR"),
                constantService.get("ERROR_" + errorCode.name()));
    }

    public void setMessage(Model model, String message) {
        model.addAttribute(constantService.getString("MODEL_KEY_MESSAGE"), message);
    }

}
