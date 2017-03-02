package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.forms.InventoryModificationForm;
import com.clovercoin.pillowing.response.AutoCompleteResponse;
import com.clovercoin.pillowing.response.AutoCompleteSuggestion;
import com.clovercoin.pillowing.service.ClientService;
import com.clovercoin.pillowing.service.ItemService;
import com.clovercoin.pillowing.service.UserService;
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

    @Autowired
    private UserService userService;

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

    @RequestMapping(value = "/search-items", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AutoCompleteResponse searchItems(@RequestParam("name") String name) {
        Page<Item> matchingItems = itemService.searchItemsByName(name, 0);
        AutoCompleteResponse response = new AutoCompleteResponse();
        matchingItems.getContent()
                .forEach(item -> {
                    AutoCompleteSuggestion suggestion = new AutoCompleteSuggestion(item.getName());
                    suggestion.getData().put("id", item.getId());
                    response.getSuggestions().add(suggestion);
                });
        return response;
    }

    @RequestMapping(value = "/search-users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AutoCompleteResponse searchUsers(@RequestParam("name") String name) {
        Page<User> matchingUsers = userService.searchUsersByUsername(name, 0);
        AutoCompleteResponse response = new AutoCompleteResponse();
        matchingUsers.getContent()
                .forEach(user -> {
                    AutoCompleteSuggestion suggestion = new AutoCompleteSuggestion(user.getUsername());
                    suggestion.getData().put("id", user.getId());
                    response.getSuggestions().add(suggestion);
                });
        return response;
    }

    @RequestMapping(value = "/get-inventory-line", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public InventoryLine getInventoryLine(@RequestParam("clientId") Long clientId, @RequestParam("itemId") Long itemId) {
        Item item = itemService.getById(itemId);
        Client client = clientService.getById(clientId);

        InventoryLine result = clientService.getInventoryLine(client, item);
        if (result == null) {
            result = new InventoryLine();
            result.setClient(client);
            result.setItem(item);
            result.setQuantity(0);
        }

        return result;
    }
}
