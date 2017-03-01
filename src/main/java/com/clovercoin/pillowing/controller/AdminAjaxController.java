package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.forms.InventoryModificationForm;
import com.clovercoin.pillowing.response.AutoCompleteResponse;
import com.clovercoin.pillowing.response.AutoCompleteSuggestion;
import com.clovercoin.pillowing.service.ClientService;
import com.clovercoin.pillowing.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

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

    @RequestMapping(value = "/updateItem", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object updateItem(Item item, HttpServletResponse response) {
        Item sameName = itemService.getByName(item.getName());
        Item existingItem = itemService.getById(item.getId());

        //id not found
        if (existingItem == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "{}";
        }

        //id found, already has given name
        if (item.getName().equals(existingItem.getName())) {
            return item;
        }

        //different id found, same name
        if (sameName != null) {
            response.setStatus(HttpStatus.CONFLICT.value());
            return "{}";
        }

        existingItem.setName(item.getName());

        return itemService.saveItem(existingItem);
    }

    @RequestMapping(value = "/search-clients", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AutoCompleteResponse searchClients(@RequestParam("name") String name) {
        Page<Client> matchingClients = clientService.searchClientsByName(name, 0);
        AutoCompleteResponse response = new AutoCompleteResponse();
        matchingClients.getContent()
                .forEach(client -> {
                    AutoCompleteSuggestion suggestion = new AutoCompleteSuggestion(client.getName());
                    suggestion.getData().put("id", client.getId());
                    response.getSuggestions().add(suggestion);
                });
        return response;
    }
}
