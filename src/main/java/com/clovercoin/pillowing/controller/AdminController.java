package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.constant.Action;
import com.clovercoin.pillowing.constant.ErrorCode;
import com.clovercoin.pillowing.constant.ItemType;
import com.clovercoin.pillowing.constant.Status;
import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.forms.UserAddForm;
import com.clovercoin.pillowing.repository.InventoryLineRepository;
import com.clovercoin.pillowing.service.ClientService;
import com.clovercoin.pillowing.service.ControllerHelper;
import com.clovercoin.pillowing.service.ItemService;
import com.clovercoin.pillowing.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@Log
public class AdminController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ControllerHelper controllerHelper;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Autowired
    private InventoryLineRepository inventoryLineRepository;

    @RequestMapping("/")
    public String index() {
        return "admin/index";
    }

    @RequestMapping(value = {"/clients", "/clients/{page}"}, method = RequestMethod.GET)
    public String client(@PathVariable("page") Optional<Integer> page, Model model) {
        Page<Client> dataPage = clientService.getPage(page.orElse(1) - 1);
        model.addAttribute("page", page.orElse(1));
        model.addAttribute("clientPage", dataPage);

        return "admin/client/list-clients";
    }

    @RequestMapping(value = "/client/add", method = RequestMethod.GET)
    public String addClient(Model model) {
        model.addAttribute("client", new Client());

        return "admin/client/add-client";
    }

    @RequestMapping(value = {"/client/{id}", "/client/{id}/inventory/{inventoryPage}/currency/{currencyPage}"}, method = RequestMethod.GET)
    public Object viewClient(@PathVariable("id") Long id,
                             @PathVariable("inventoryPage") Optional<Integer> requestedInventoryPage,
                             @PathVariable("currencyPage") Optional<Integer> requestedCurrencyPage,
                             Model model) {
        Integer inventoryPage = requestedInventoryPage.orElse(1) - 1;
        Integer currencyPage = requestedCurrencyPage.orElse(1) - 1;
        Client client = clientService.getById(id);
        Page<InventoryLine> inventory = clientService.getInventoryPage(client, inventoryPage);
        Page<InventoryLine> currency = clientService.getCurrencyPage(client, currencyPage);

        model.addAttribute("client", client);
        model.addAttribute("inventoryPage", inventory);
        model.addAttribute("currencyPage", currency);

        model.addAttribute("inventoryPageNumber", inventoryPage+1);
        model.addAttribute("currencyPageNumber", currencyPage+1);

        return "admin/client/view-client";
    }

    @RequestMapping(value = "/foo", method = RequestMethod.GET)
    @ResponseBody
    public Object foo() {
        Client client = new Client();
        client.setName("bob");
        client = clientService.saveClient(client);

        for (int i = 0; i < 20; i++) {
            Item item = new Item();
            item.setName("item-" + i);
            item.setItemType(i < 10 ? ItemType.ITEM : ItemType.CURRENCY);
            itemService.saveItem(item);
        }

        int page = 0;
        Page<Item> items = itemService.getPage(0);
        int offset = 0;
        for (int i = 0; i < 20; i++) {
            if (i-offset == items.getSize()) {
                offset += items.getSize();
                items = itemService.getPage(++page);
            }
            Item item = items.getContent().get(i-offset);
            InventoryLine inventoryLine = new InventoryLine();
            inventoryLine.setClient(client);
            inventoryLine.setItem(item);
            inventoryLine.setQuantity(2);
            clientService.saveInventoryLine(inventoryLine);
        }

        //return "complete";
        return inventoryLineRepository.findByClientAndItemItemType(client, ItemType.ITEM, new PageRequest(0, 20, Sort.Direction.ASC, "item.name"));
    }

    @RequestMapping(value = "/client/add", method = RequestMethod.POST)
    public String addClient(Client client, Model model) {
        controllerHelper.setAction(model, Action.ADD_SUBMIT);
        model.addAttribute("client_name", client.getName());

        try {
            clientService.saveClient(client);
            client = new Client();
            controllerHelper.setStatus(model, Status.SUCCESS);
        } catch (DuplicateKeyException dke) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.DUPLICATE_KEY);
        } catch (Exception e) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.UNKNOWN);
        }

        model.addAttribute("client", client);
        return "admin/client/add-client";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String user(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user/list-users";
    }

    @RequestMapping(value = "/user/add", method = RequestMethod.GET)
    public String addUser(Model model) {
        UserAddForm userAddForm = new UserAddForm();
        controllerHelper.setAction(model, Action.ADD);
        model.addAttribute("userForm", userAddForm);

        return "admin/user/add-user";
    }

    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public String addUser(UserAddForm userAddForm, Model model) {
        User user = userService.createUserFromForm(userAddForm);
        controllerHelper.setAction(model, Action.ADD_SUBMIT);
        try {
            user = userService.createUser(user);
            controllerHelper.setStatus(model, Status.SUCCESS);
            model.addAttribute("user_email", user.getEmail());
            model.addAttribute("user_username", user.getUsername());
            model.addAttribute("userForm", new UserAddForm());
        } catch (DuplicateKeyException dke) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.DUPLICATE_KEY);
            model.addAttribute("userForm", userAddForm);
        } catch (Exception e) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.UNKNOWN);
            model.addAttribute("userForm", userAddForm);
        }

        return "admin/user/add-user";
    }

    @RequestMapping(value = {"/items", "/items/{page}"}, method = RequestMethod.GET)
    public String item(@PathVariable("page") Optional<Integer> page, Model model) {
        Page<Item> dataPage = itemService.getPage(page.orElse(1) - 1);
        model.addAttribute("page", page.orElse(1));
        model.addAttribute("itemPage", dataPage);

        return "admin/item/list-items";
    }

    @RequestMapping(value = "/item/add", method = RequestMethod.GET)
    public String addItem(Model model) {
        model.addAttribute("item", new Item());
        controllerHelper.setAction(model, Action.ADD);
        return "admin/item/add-item";
    }

    @RequestMapping(value = "/item/add", method = RequestMethod.POST)
    public String addItem(Model model, Item item) {
        model.addAttribute("item_name", item.getName());
        controllerHelper.setAction(model, Action.ADD_SUBMIT);
        try {
            itemService.saveItem(item);
            model.addAttribute("item", new Item());
            model.addAttribute("item_name", item.getName());
            controllerHelper.setStatus(model, Status.SUCCESS);
        } catch (DuplicateKeyException dke) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.DUPLICATE_KEY);
            model.addAttribute("item", item);
        } catch (Exception e) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.UNKNOWN);
            model.addAttribute("item", item);
        }
        return "admin/item/add-item";
    }

}
