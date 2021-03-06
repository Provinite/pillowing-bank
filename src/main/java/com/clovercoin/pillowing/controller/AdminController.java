package com.clovercoin.pillowing.controller;

import com.clovercoin.pillowing.constant.Action;
import com.clovercoin.pillowing.constant.ErrorCode;
import com.clovercoin.pillowing.constant.ItemType;
import com.clovercoin.pillowing.constant.Status;
import com.clovercoin.pillowing.entity.Client;
import com.clovercoin.pillowing.entity.InventoryLine;
import com.clovercoin.pillowing.entity.Item;
import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.forms.InventoryModificationForm;
import com.clovercoin.pillowing.forms.UserAddForm;
import com.clovercoin.pillowing.repository.InventoryLineRepository;
import com.clovercoin.pillowing.service.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    private SecurityService securityService;
    @Autowired
    private InventoryLineRepository inventoryLineRepository;
    @Autowired
    private TransactionService transactionService;

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
        controllerHelper.setAction(model, Action.ADD);
        return "admin/client/add-client";
    }

    @RequestMapping(value = "/client/add", method = RequestMethod.POST)
    public String addClient(Client client, Model model) {
        controllerHelper.setAction(model, Action.ADD_SUBMIT);
        model.addAttribute("client_name", client.getName());

        if (doClientSave(model, client)) {
            client = new Client();
        }

        model.addAttribute("client", client);
        return "admin/client/add-client";
    }

    @RequestMapping(value = "/client/{id}/edit", method = RequestMethod.GET)
    public String editClient(@PathVariable("id") Long clientId, Model model) {
        Client client = clientService.getById(clientId);
        model.addAttribute("client", client);
        controllerHelper.setAction(model, Action.EDIT);
        return "admin/client/add-client";
    }

    @RequestMapping(value = "/client/{id}/edit", method = RequestMethod.POST)
    public String editClient(@PathVariable("id") Long clientId, Client client, Model model) {
        controllerHelper.setAction(model, Action.EDIT_SUBMIT);
        model.addAttribute("client_name", client.getName());

        Client existingClient = clientService.getById(clientId);
        existingClient.setName(client.getName());
        existingClient.setNote(client.getNote());

        if (doClientSave(model, existingClient)) {
            client = clientService.getById(clientId);
        }

        model.addAttribute("client", client);
        return "admin/client/add-client";
    }

    private boolean doClientSave(Model model, Client client) {
        try {
            clientService.saveClient(client);
            controllerHelper.setStatus(model, Status.SUCCESS);
        } catch (DuplicateKeyException dke) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.DUPLICATE_KEY);
            return false;
        } catch (Exception e) {
            controllerHelper.setStatus(model, Status.FAILURE);
            controllerHelper.setError(model, ErrorCode.UNKNOWN);
            return false;
        }
        return true;
    }

    @RequestMapping(value = {"/client/{id}", "/client/{id}/inventory/{inventoryPage}/currency/{currencyPage}"}, method = RequestMethod.GET)
    public String viewClient(@PathVariable("id") Long id,
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

    @RequestMapping(value = "/client/{id}/add-item", method = RequestMethod.GET)
    public String addItemToInventory(@PathVariable("id") Long id, @RequestParam(value = "type", defaultValue = "item") String type, Model model) {
        Client client = clientService.getById(id);
        InventoryModificationForm inventoryModificationForm = new InventoryModificationForm();
        inventoryModificationForm.setClientId(client.getId());
        if (type.toLowerCase().equals("currency")) {
            model.addAttribute("itemType", "currency");
        } else {
            model.addAttribute("itemType", "item");
        }
        model.addAttribute("form", inventoryModificationForm);
        model.addAttribute("client", client);
        return "admin/client/add-item";
    }

    @RequestMapping(value = "/client/{id}/add-item", method = RequestMethod.POST)
    public String addItemToInventory(@PathVariable("id") Long clientId, InventoryModificationForm form, Model model) {
        Client client = clientService.getById(form.getClientId());
        Item item = itemService.getById(form.getItemId());
        Integer quantity = form.getQuantity();
        String note = form.getNote();

        clientService.updateInventoryQuantity(client, item, quantity, note);
        controllerHelper.setAction(model, Action.ADD_SUBMIT);
        controllerHelper.setStatus(model, Status.SUCCESS);

        form = new InventoryModificationForm();
        form.setClientId(clientId);
        model.addAttribute("form", form);
        model.addAttribute("client", client);

        return "admin/client/add-item";
    }

    @RequestMapping(value = "/foo", method = RequestMethod.GET)
    @ResponseBody
    public Object foo() {
        Client client = new Client();
        client.setName("bob");
        client = clientService.saveClient(client);

        String[] clients = {"alice","bobert","charlie","chuck","dan","change","phil","lars"};
        for (String s : clients) {
            Client c = new Client();
            c.setName(s);
            clientService.saveClient(c);
        }

        for (int i = 0; i < 100; i++) {
            Item item = new Item();
            item.setName("item-" + i);
            item.setItemType(i < 10 ? ItemType.ITEM : ItemType.CURRENCY);
            itemService.saveItem(item);
        }

        int page = 0;
        Page<Item> items = itemService.getPage(0);
        int offset = 0;
        for (int i = 0; i < 100; i++) {
            if (i-offset == items.getSize()) {
                offset += items.getSize();
                items = itemService.getPage(++page);
            }
            clientService.updateInventoryQuantity(client, items.getContent().get(i-offset), 2, "User won a big fat raffle. So much winning. Very wow!");
        }

        return inventoryLineRepository.findByClientAndItemItemType(client, ItemType.ITEM, new PageRequest(0, 20, Sort.Direction.ASC, "item.name"));
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

    @PreAuthorize("!@userService.getById(#id).email.equals(principal.username)")
    @RequestMapping(value = "/user/{id}/disable-user", method = RequestMethod.GET)
    public String disableUser(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        securityService.expireUserSessions(user);
        user.setActive(0);
        userService.saveUser(user, false);
        return "redirect:/admin/users";
    }

    @RequestMapping(value = "/user/{id}/enable-user", method = RequestMethod.GET)
    public String enableUser(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        user.setActive(1);
        userService.saveUser(user, false);
        return "redirect:/admin/users";
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

    @RequestMapping(value = {"/items", "/items/{itemPage}/currencies/{currencyPage}"}, method = RequestMethod.GET)
    public String item(
            @PathVariable("itemPage") Optional<Integer> requestedItemPage,
            @PathVariable("currencyPage") Optional<Integer> requestedCurrencyPage,
            Model model) {
        Page<Item> dataPage = itemService.getItemPage(requestedItemPage.orElse(1) - 1);
        Page<Item> currencyPage = itemService.getCurrencyPage(requestedCurrencyPage.orElse(1) - 1);
        model.addAttribute("itemPageNumber", requestedItemPage.orElse(1));
        model.addAttribute("itemPage", dataPage);
        model.addAttribute("currencyPageNumber", requestedCurrencyPage.orElse(1));
        model.addAttribute("currencyPage", currencyPage);

        return "admin/item/list-items";
    }

    @RequestMapping(value = "/item/addCurrency", method = RequestMethod.GET)
    public String addCurrency(Model model) {
        Item item = new Item();
        item.setItemType(ItemType.CURRENCY);
        model.addAttribute("item", item);
        controllerHelper.setAction(model, Action.ADD);
        return "admin/item/add-item";
    }

    @RequestMapping(value = "/item/addCurrency", method = RequestMethod.POST)
    public String addCurrency(Model model, Item item) {
        Item blankItem = new Item();
        blankItem.setItemType(ItemType.CURRENCY);
        handleItemSave(item, blankItem, model);
        return "admin/item/add-item";
    }

    @RequestMapping(value = "/item/add", method = RequestMethod.GET)
    public String addItem(Model model) {
        Item item = new Item();
        item.setItemType(ItemType.ITEM);
        model.addAttribute("item", item);
        controllerHelper.setAction(model, Action.ADD);
        return "admin/item/add-item";
    }

    @RequestMapping(value = "/item/add", method = RequestMethod.POST)
    public String addItem(Model model, Item item) {
        Item blankItem = new Item();
        blankItem.setItemType(ItemType.ITEM);
        handleItemSave(item, blankItem, model);
        return "admin/item/add-item";
    }

    private void handleItemSave(Item item, Item blankItem, Model model) {
        controllerHelper.setAction(model, Action.ADD_SUBMIT);
        model.addAttribute("item_name", item.getName());
        try {
            itemService.saveItem(item);
            model.addAttribute("item", blankItem);
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
    }

    @RequestMapping(value = {"/audit", "/audit/{page}"}, method = RequestMethod.GET)
    public String audit(@PathVariable("page") Optional<Integer> page, Model model) {
        Integer requestedPage = page.orElse(1) - 1;
        model.addAttribute("transactionPage", transactionService.getPage(requestedPage));
        return "admin/audit/list-transactions";
    }

    @RequestMapping(value = {"/audit/user/{id}", "/audit/user/{id}/{page}"}, method = RequestMethod.GET)
    public String auditUser(@PathVariable("id") Long userId,
                            @PathVariable("page") Optional<Integer> page,
                            Model model) {
        Integer requestedPage = page.orElse(1) - 1;
        User user = userService.getById(userId);
        model.addAttribute("transactionPage", transactionService.searchByUser(user, requestedPage));
        model.addAttribute("user", user);

        return "admin/audit/list-transactions";
    }

    @RequestMapping(value = {"/audit/client/{id}", "/audit/client/{id}/{page}"}, method = RequestMethod.GET)
    public String auditClient(@PathVariable("id") Long clientId,
                            @PathVariable("page") Optional<Integer> page,
                            Model model) {
        Integer requestedPage = page.orElse(1) - 1;
        Client client = clientService.getById(clientId);
        model.addAttribute("transactionPage", transactionService.searchByClient(client, requestedPage));
        model.addAttribute("client", client);

        return "admin/audit/list-transactions";
    }

}
