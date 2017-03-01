package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.forms.InventoryModificationForm;
import com.clovercoin.pillowing.service.ClientService;
import com.clovercoin.pillowing.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/ajax")
public class AdminAjaxController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/modifyInventory", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object modifyInventory(InventoryModificationForm form) {
        Client client = clientService.getById(form.getClientId());
        Item item = itemService.getById(form.getItemId());
        clientService.updateInventoryQuantity(client, item, form.getQuantity(), form.getNote());
        InventoryLine result = clientService.getInventoryLine(client, item);
        if (result != null)
            return result;

        return "{}";
    }
}
